/*
 * Joseph Zhang
 * 5/10/2019
 * 
 * ReadMusicFreqData.java
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Reads the music in the file
public class ReadMusicFreqData {
    private List<List<Float>> freqArray = new ArrayList<List<Float>>();
    
    ReadMusicFreqData(String dataFilePath) {
		BufferedReader br = null;
		
		try {
			FileReader file = new FileReader(dataFilePath);
			br = new BufferedReader(file);

			String line;
	        while ((line = br.readLine()) != null) {
				String[] freqs_str = line.split(",");
				
				List<Float> freqs_lst = new ArrayList<Float>();

				for (String freq : freqs_str)
					freqs_lst.add(Float.parseFloat(freq));
				
				freqArray.add(freqs_lst);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
    
    //Gets the timeSegmentSize
    public int GetTimeSegmentSize() {
    	return freqArray.size();
    }
    
    //Gets FreqSegmentSize
    public int GetFreqSegmentSize() {
    	return freqArray.get(0).size();
    }
    
    //Gets Freq Data
    public List<Float> GetFreqData(int timeSegment) {
    	return freqArray.get(timeSegment);
    }
}
