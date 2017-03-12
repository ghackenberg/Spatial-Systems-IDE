package de.tum.imomesa.tracker.writers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.tracker.events.WriterEndEvent;
import de.tum.imomesa.tracker.events.WriterStartEvent;
import de.tum.imomesa.tracker.structures.Frame;

public abstract class AbstractWriter extends Thread {
	
	private BlockingQueue<Frame> frames = new LinkedBlockingQueue<>(20);
	private File output;

	public AbstractWriter(String name, File output) {
		super(name);
		
		this.output = output;
		
		output.getParentFile().mkdirs();
	}
	
	public BlockingQueue<Frame> getFrames() {
		return frames;
	}
	
	@Override
	public final void run() {
		try {
			EventBus.getInstance().publish(new WriterStartEvent(this));
			
			// Setup file output
			FileChannelWrapper sink = NIOUtils.writableFileChannel(output);
	
			// Setup movie muxer
			MP4Muxer muxer = new MP4Muxer(sink, Brand.MOV);
			FramesMP4MuxerTrack track = muxer.addTrackForCompressed(TrackType.VIDEO, 1000);
			
			// Setup encoder/trasformer
			H264Encoder encoder = new H264Encoder();
			RgbToYuv420 transform = new RgbToYuv420(0, 0);
			
			// Create lists
			List<ByteBuffer> spsList = new ArrayList<ByteBuffer>();
	        List<ByteBuffer> ppsList = new ArrayList<ByteBuffer>();
			
	        // Current frame
	        Frame frame;
	        // Process frames
			while ((frame = frames.take()).getImage() != null) {
				// Obtain image
				BufferedImage image_rgb = frame.getImage();
				/*
				// Write screenshot to PNG
				ImageIO.write(image_rgb, "jpg", new File(output.getParentFile(), output.getName().substring(0, output.getName().indexOf('.')) + "_" + frame.getNumber() + ".jpg"));
				*/
				// Convert screenshot to YUV420
				Picture image_yuv = Picture.create(image_rgb.getWidth(), image_rgb.getHeight(), ColorSpace.YUV420);
				
				Arrays.fill(image_yuv.getData()[0], 0);
				Arrays.fill(image_yuv.getData()[1], 0);
				Arrays.fill(image_yuv.getData()[2], 0);
				
				transform.transform(AWTUtil.fromBufferedImage(image_rgb), image_yuv);
				
				// Create buffer
				ByteBuffer image_buffer = ByteBuffer.allocate(image_rgb.getWidth() * image_rgb.getHeight() * 3);
	
				// Encode screenshot
				ByteBuffer image_packet = encoder.encodeFrame(image_buffer, image_yuv);
				
				// Clear lists
				spsList.clear();
				ppsList.clear();
				
				// Encode packet
				H264Utils.encodeMOVPacket(image_packet, spsList, ppsList);
				
				// Add frame
				track.addFrame(new MP4Packet(image_packet, frame.getTimestamp(), 1000, frame.getDurartion(), frame.getNumber(), true, null, frame.getTimestamp(), 0));
				
				// Debug output
				System.out.println(this + " adding frame at position " + frame.getTimestamp() + " for duration " + frame.getDurartion());
				
			}
			/*
			// Debug output
			System.out.println(this + " adding sample entry");
			*/
			// Add video track sample
			track.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));
			/*
			// Debug output
			System.out.println(this + " writing header");
			*/
			// Mux video tracks
			muxer.writeHeader();
			// Close file channel
			NIOUtils.closeQuietly(sink);
			
			EventBus.getInstance().publish(new WriterEndEvent(this));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
