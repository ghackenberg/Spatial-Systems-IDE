package de.tum.imomesa.analyzer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
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

import de.tum.imomesa.database.client.Client;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;

public class Snapshot {

	private Client client;
	private File base;

	private Map<Project, File> folders = new HashMap<>();
	private Map<Project, List<File>> files = new HashMap<>();
	private int id = 0;

	public Snapshot(Client client, File base) {
		this.client = client;
		this.base = base;

		if (!base.isDirectory() && !base.mkdirs()) {
			throw new IllegalStateException("Base is not a director " + base.getAbsolutePath());
		}
	}

	// CREATE

	public void create(long timestamp) {
		System.out.println("Creating snapshot " + id);

		// Process workspaces
		List<Workspace> workspaces = client.getObjects(Workspace.class);

		for (Workspace workspace : workspaces) {
			for (Project project : workspace.getProjects()) {
				process(project, timestamp);
			}
		}

		id++;
	}

	// PROCESS

	private void process(Project project, long timestamp) {
		if (project.getComponent() != null) {
			try {
				// Get target folder
				File folder = new File(base, "Project " + client.getKey(project));

				if (!folders.containsKey(project)) {
					folders.put(project, folder);
					files.put(project, new ArrayList<>());
				}
				
				if (!folder.isDirectory() && !folder.mkdirs()) {
					throw new IllegalStateException("Folder cannot be used " + folder.getAbsolutePath());
				}

				File tempPovFile = new File("Snapshot_" + id + ".pov");
				File tempPngFile = new File("Snapshot_" + id + ".png");

				File targetPovFile = new File(folder, tempPovFile.getName());
				File targetPngFile = new File(folder, tempPngFile.getName());

				double cy = 85;
				double cx = Math.sqrt(2 * cy * cy) * Math.sin(2 * Math.PI * id / 100);
				double cz = Math.sqrt(2 * cy * cy) * Math.cos(2 * Math.PI * id / 100);
				
				FileUtils.write(tempPovFile, Povray.render(project.getComponent(), timestamp, cx, cy, cz));

				Runtime runtime = Runtime.getRuntime();

				runtime.exec("pvengine /EXIT /RENDER \"" + tempPovFile.getName() + "\"").waitFor();

				Files.move(tempPovFile.toPath(), targetPovFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Files.move(tempPngFile.toPath(), targetPngFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				files.get(project).add(targetPngFile);
			} catch (IOException e) {
				System.out.println("Snapshot: Cannot render component " + e.getMessage());
			} catch (InterruptedException e) {
				System.out.println("Snapshot: Cannot render component " + e.getMessage());
			}
		} else {
			System.out.println("Snapshot: Folder is null for " + project);
		}
	}

	// FINISH

	public void finish() {
		for (Entry<Project, File> entry : folders.entrySet()) {
			Project project = entry.getKey();
			File folder = entry.getValue();
			List<File> files = this.files.get(project);

			try {
				mov(project, folder, files);
			} catch (IOException e) {
				System.out.println("Snapshot cannot create mov output " + e.getMessage());
			}
			html(project, folder, files);
		}
	}

	private void mov(Project project, File folder, List<File> files) throws IOException {
		// Setup file output
		FileChannelWrapper sink = NIOUtils.writableFileChannel(new File(folder, "Snapshots.mov"));

		// Setup movie muxer
		MP4Muxer muxer = new MP4Muxer(sink, Brand.MOV);
		FramesMP4MuxerTrack track = muxer.addTrackForCompressed(TrackType.VIDEO, 1000);

		// Setup encoder/trasformer
		H264Encoder encoder = new H264Encoder();
		RgbToYuv420 transform = new RgbToYuv420(0, 0);

		// Create lists
		List<ByteBuffer> spsList = new ArrayList<ByteBuffer>();
		List<ByteBuffer> ppsList = new ArrayList<ByteBuffer>();

		int number = 0;
		int timestamp = 0;
		int duration = Math.min(100, Math.max(1, 1000 * 60 * 5 / files.size()));

		// Process frames
		for (File file : files) {
			// Obtain image
			BufferedImage image_rgb = ImageIO.read(file);

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
			track.addFrame(new MP4Packet(image_packet, timestamp, 1000, duration, number, true, null, timestamp, 0));

			// Debug output
			System.out.println(this + " adding frame at position " + timestamp + " for duration " + duration);

			number++;
			timestamp += duration;
		}
		/*
		 * // Debug output System.out.println(this + " adding sample entry");
		 */
		// Add video track sample
		track.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));
		/*
		 * // Debug output System.out.println(this + " writing header");
		 */
		// Mux video tracks
		muxer.writeHeader();
		// Close file channel
		NIOUtils.closeQuietly(sink);
	}

	private void html(Project project, File folder, List<File> files) {
		String html = "";

		html += "<html>\n";
		html += "<head>\n";
		html += "<style>\n";
		html += "img { width: 300px; }\n";
		html += "</style>\n";
		html += "</head>\n";
		html += "<body>\n";
		html += "<table>\n";

		int cols = (int) Math.ceil(Math.sqrt(files.size()));
		int rows = (int) Math.ceil(files.size() / cols);

		for (int row = 0; row < rows; row++) {
			html += "<tr>\n";
			for (int col = 0; col < cols; col++) {
				int index = row * cols + col;

				html += "<td>\n";
				if (index < files.size()) {
					html += "<img src=\"" + files.get(index).getName() + "\"/>\n";
				}
				html += "</td>\n";
			}
			html += "</tr>\n";
		}

		html += "</table>";
		html += "</body>\n";
		html += "</html>\n";

		try {
			FileUtils.write(new File(folder, "Snapshots.html"), html);
		} catch (IOException e) {
			System.out.println("Snapshot: Could not create snapshot table " + e.getMessage());
		}
	}

}
