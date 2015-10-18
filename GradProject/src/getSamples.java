import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.media.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import org.jtransforms.dct.*;




public class getSamples extends JPanel implements ActionListener{
	static private final String newline = "\n";
	static private final int FOURIER_ARRAY_LENGTH = 16;
	
	JButton open, save;
	JTextArea log;
	JFileChooser fc;
	
	public static double[] copyFromIntArray(int[] source) {
	    double[] dest = new double[source.length];
	    for(int i=0; i<source.length; i++) {
	        dest[i] = source[i];
	    }
	    return dest;
	}
	
	public getSamples() {
		fc = new JFileChooser();
		open = new JButton("open a song ...");
		open.addActionListener(this);
	}
	
	protected int getSixteenBitSample(int high, int low) {
	    return ((high << 8) + (low & 0x00ff)) ;
	} 
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == open) {
			int returnVal = fc.showOpenDialog(getSamples.this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				try {
					AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
					int frameLength = (int) audioInputStream.getFrameLength();
					int frameSize = (int) audioInputStream.getFormat().getFrameSize();
					byte[] eightBitByteArray = new byte[frameLength * frameSize];
					int result = audioInputStream.read(eightBitByteArray);
					int channels = audioInputStream.getFormat().getChannels();
					int[][] samples = new int[channels][frameLength];
					DoubleDCT_1D DFTransformer = new DoubleDCT_1D(FOURIER_ARRAY_LENGTH);
					
					double[][] transformValues = new double[channels][(int) Math.floor(frameLength/FOURIER_ARRAY_LENGTH)];
					
					int sampleIndex = 0;
					for(int t=0; t < eightBitByteArray.length;) {
						for(int channel = 0; channel < channels; channel++){
							int low = (int) eightBitByteArray[t];
							t++;
							int high = (int) eightBitByteArray[t];
							t++;
							int sample = getSixteenBitSample(high,low);
							samples[channel][sampleIndex] = sample;
						}
						sampleIndex++;
					}
					for (int i = 0; i<samples.length; i++){
						for (int j=0; j<transformValues[i].length;j++){
							double[] a = new double[FOURIER_ARRAY_LENGTH];
							for(int k=0;k<FOURIER_ARRAY_LENGTH;k++){
								a[k] = (double) samples[i][4*j+k];
							}
							DFTransformer.forward(a, false);
							for(int k=0;k<FOURIER_ARRAY_LENGTH;k++){
								System.out.println(a[k]);
							}
						}
					}
				} catch (UnsupportedAudioFileException | IOException e1) {
					System.out.println("please use supported audio types");
					e1.printStackTrace();
				}
				
			}
		}
		
	}
	
}
