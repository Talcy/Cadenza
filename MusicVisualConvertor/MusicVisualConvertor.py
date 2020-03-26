import sys
import scipy
import scipy.io.wavfile
import scipy.signal
import time
from subprocess import call
from pathlib import Path

# Music file input
# music_path = sys.argv[1]
music_path = 'Bitch_Lasagna.mp3'

# Toll of mp3 to wav format
lame_path = 'lame.exe'
# Number of samples per DFT (better to be a power of 2) (higher: > frequency resolution, < time resolution)
fftlength = 2048
# Percentage of frequencies to show (Removes higher frequencies) Range = [0, 1]
percentage_visual_freq = 0.3
# Pecentile of amplitude that fills the entire height of screen Range = (0, 100]
max_height_percentile = 80.0    # 99.85
# Max display height unit
display_height_unit = 640
# Max display width unit
display_width_unit = 10

# Check music file
if music_path[len(music_path)-4:] == '.mp3' and Path(lame_path).is_file():
    if Path(music_path).is_file() != True:
        print("Cannot find input file: ", music_path)
        sys.exit()

    print('Attempting to convert mp3 file into wav ...')
    call(["lame", "--decode", music_path, music_path[:len(music_path)-4]+'.wav'], shell=True)
    music_path = music_path[:len(music_path)-4]+'.wav'

if music_path[len(music_path)-4:] != '.wav' or Path(music_path).is_file() != True:
    print("Cannot find input file: ", music_path)
    sys.exit()

# Read music wave file
data_file = music_path
music_path = Path(music_path)

# Return sample rate of wave file, data array
try:
    print('Read the sound file ...')
    sample_rate, original_signal = scipy.io.wavfile.read(music_path)
except:
    raise Exception('Error parsing sound file')

# Combining both channels by averaging
print("Convert stereo to mono data ...")
music_mono = scipy.mean(original_signal, axis=1)

# Compute a spectrogram with consecutive Fourier transforms
# f <ndarray > : Array of sample frequencies.
# t <ndarray> : Array of segment times.
# Sxx <ndarray> : Spectrogram of x. By default, the last axis of Sxx corresponds to the segment times.
# f, t are axis, Sxx[frequency][time]
print('Fourier transforming ...')
sample_freqs, segment_times, Sxx = scipy.signal.spectrogram(music_mono, sample_rate, nperseg=fftlength)                  

num_of_visual_freq = int(len(sample_freqs)*percentage_visual_freq + 0.5)
Sxx = Sxx[:num_of_visual_freq-2].transpose()
sample_freqs = sample_freqs[:num_of_visual_freq-2]

segment_times_len = len(segment_times)
sample_freqs_len = len(sample_freqs)

print('Segment times = ', segment_times_len, ', visual frequencies = ', sample_freqs_len)

# The qth percentile is a value such that at least q percent of the observations is less than or equal 
# to this value and at least (100−p) percent of the observations is greater than or equal to this value.
height_scale_factor = display_height_unit / scipy.percentile(Sxx, max_height_percentile)

# Interval of time segment
time_interval = segment_times[1] - segment_times[0]

print(sample_freqs.max(), sample_freqs.min())
print('Height scale factor = ', height_scale_factor, ', time interval = ', time_interval)

# Pooling both freqs and time segments
def pooling(mat,ksize,method='max',pad=False):
    '''Non-overlapping pooling on 2D or 3D data.

    <mat>: ndarray, input array to pool.
    <ksize>: tuple of 2, kernel size in (ky, kx).
    <method>: str, 'max for max-pooling, 
                   'mean' for mean-pooling.
    <pad>: bool, pad <mat> or not. If no pad, output has size
           n//f, n being <mat> size, f being kernel size.
           if pad, output has size ceil(n/f).

    Return <result>: pooled matrix.
    '''
    m, n = mat.shape[:2]
    ky, kx = ksize

    _ceil=lambda x,y: int(scipy.ceil(x/float(y)))

    if pad:
        ny = _ceil(m,ky)
        nx = _ceil(n,kx)
        size = (ny * ky, nx * kx) + mat.shape[2:]
        mat_pad = scipy.full(size,scipy.nan)
        mat_pad[:m,:n,...] = mat
    else:
        ny = m // ky
        nx = n // kx
        mat_pad = mat[:ny*ky, :nx*kx, ...]

    new_shape = (ny, ky, nx, kx) + mat.shape[2:]

    if method=='max':
        result=scipy.nanmax(mat_pad.reshape(new_shape),axis=(1,3))
    else:
        result=scipy.nanmean(mat_pad.reshape(new_shape),axis=(1,3))

    return result

# Key size for pooling
kx = (int)(sample_freqs_len // display_width_unit)
ky = (int)(1.0 // time_interval)

sxx_pooled = pooling(Sxx,(ky,kx),method='mean')
segment_times_len, sample_freqs_len = sxx_pooled.shape[:2]

# Calculate all freqs in all time segment
min_height = 2

for index_time in range(segment_times_len):
    # print(index_time, '/', segment_times_len)
    sum_height = 0.0
    for index_freq, frequency in enumerate(sxx_pooled[index_time]):
        value = max(sxx_pooled[index_time][index_freq], min_height / height_scale_factor)
        height = value * height_scale_factor

        if height > min_height:
            sxx_pooled[index_time][index_freq] = height
        else:
            sxx_pooled[index_time][index_freq] = 0.0

        sum_height = sum_height + height
sum_height = sum_height / segment_times_len

# Normalization
import numpy as np 

def normalizing(x, out_range=(-1, 1), axis=None):
    domain = np.min(x, axis), np.max(x, axis)
    y = (x - (domain[1] + domain[0]) / 2) / (domain[1] - domain[0])
    return y * (out_range[1] - out_range[0]) + (out_range[1] + out_range[0]) / 2

sxx_normalized = normalizing(sxx_pooled, out_range=(0.0, display_height_unit))

# Save data to file
data_file = data_file[:len(data_file)-4]+'.csv'
scipy.savetxt(data_file, sxx_normalized, fmt='%f', delimiter=',')

# Save information to file
info_file = data_file[:len(data_file)-4]+'.txt'
with open(info_file, "w") as text_file:
    print(f"Music name: {data_file[:len(data_file)-4]}", file=text_file)
    print(f"Music length: {segment_times_len}", file=text_file)
    print(f"Music difficulty: {sum_height}", file=text_file)
