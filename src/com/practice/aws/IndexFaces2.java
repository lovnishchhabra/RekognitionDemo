package com.practice.aws;

import java.nio.ByteBuffer;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;

public class IndexFaces2 {

	public String run(String collectionId, String imageName, byte[] bytes) {
		StringBuilder res = new StringBuilder();
		AmazonRekognition rekognition = ClientFactory.createClient();
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

		IndexFacesRequest request = new IndexFacesRequest()
				.withCollectionId(collectionId)
				.withDetectionAttributes("ALL")
				.withImage(new Image().withBytes(byteBuffer))
				.withExternalImageId(imageName);
		IndexFacesResult result = rekognition.indexFaces(request);

		List<FaceRecord> faceRecords = result.getFaceRecords();
		for (FaceRecord rec : faceRecords) {
			FaceDetail faceDetail = rec.getFaceDetail();
			BoundingBox bb = faceDetail.getBoundingBox();
			System.out.println("Bounding box: left=" + bb.getLeft() + 
					"; top=" + bb.getTop() + 
					"; width=" + bb.getWidth() + 
					"; height=" + bb.getHeight());

			Face face = rec.getFace();                
			System.out.println("Face-ID: " + face.getFaceId() + 
					"\nImage ID: " + face.getImageId() + 
					"\nExternal Image ID: " + face.getExternalImageId() + 
					"\nConfidence: " + face.getConfidence());
			
			res.append("Bounding box: left=");
			res.append(bb.getLeft());
			res.append("; top=");
			res.append(bb.getTop());
			res.append("; width=");
			res.append(bb.getWidth());
			res.append("; height=");
			res.append(bb.getHeight());
			res.append("\nFace-ID: ");
			res.append(face.getFaceId());
			res.append("\\nImage ID: ");
			res.append(face.getImageId());
			res.append("\\nExternal Image ID: ");
			res.append(face.getExternalImageId());
			res.append("\\nConfidence: ");
			res.append(face.getConfidence());
			
		}
		return res.toString();
	}
}
