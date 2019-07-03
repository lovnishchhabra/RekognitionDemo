package com.practice.aws;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.Beard;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.Emotion;
import com.amazonaws.services.rekognition.model.EyeOpen;
import com.amazonaws.services.rekognition.model.Eyeglasses;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Gender;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.ImageQuality;
import com.amazonaws.services.rekognition.model.Landmark;
import com.amazonaws.services.rekognition.model.MouthOpen;
import com.amazonaws.services.rekognition.model.Mustache;
import com.amazonaws.services.rekognition.model.Pose;
import com.amazonaws.services.rekognition.model.Smile;
import com.amazonaws.services.rekognition.model.Sunglasses;

public class DetectFaces2 {

    public String run(String imgPath, byte[] bytes) {
    	StringBuilder res = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        AmazonRekognition rekognition = ClientFactory.createClient();
        DetectFacesRequest request = new DetectFacesRequest().withImage(new Image().withBytes(byteBuffer))
                .withAttributes(Attribute.ALL);
        DetectFacesResult result = rekognition.detectFaces(request);
        List<FaceDetail> faceDetails = result.getFaceDetails();
        List<BoundingBox> boundingBoxes = new ArrayList<>(faceDetails.size());
        for (FaceDetail faceDetail : faceDetails) {

            printFaceDetails(faceDetail, res);
            boundingBoxes.add(faceDetail.getBoundingBox());
        }
        
        BoundingBoxDrawer drawer = new BoundingBoxDrawer();
        drawer.drawBoundingBoxes(bytes, result.getOrientationCorrection(), boundingBoxes);
		return res.toString();
    }

    private void printFaceDetails(FaceDetail faceDetail, StringBuilder res) {
        System.out.println("###############");
        res.append("###############");
        AgeRange ageRange = faceDetail.getAgeRange();
        System.out.println("Age range: " + ageRange.getLow() + "-" + ageRange.getHigh());
        res.append("\nAge range: " + ageRange.getLow() + "-" + ageRange.getHigh());
        Beard beard = faceDetail.getBeard();
        System.out.println("Beard: " + beard.getValue() + "; confidence=" + beard.getConfidence());
        res.append("\nBeard: " + beard.getValue() + "; confidence=" + beard.getConfidence());
        BoundingBox bb = faceDetail.getBoundingBox();
        System.out.println("BoundingBox: left=" + bb.getLeft() + ", top=" + bb.getTop() + ", width=" + bb.getWidth()
                + ", height=" + bb.getHeight());
        res.append("\nBoundingBox: left=" + bb.getLeft() + ", top=" + bb.getTop() + ", width=" + bb.getWidth()
        + ", height=" + bb.getHeight());
        Float confidence = faceDetail.getConfidence();
        System.out.println("Confidence: " + confidence);
        res.append("\nConfidence: " + confidence);
        List<Emotion> emotions = faceDetail.getEmotions();
        for (Emotion emotion : emotions) {
            System.out.println("Emotion: " + emotion.getType() + "; confidence=" + emotion.getConfidence());
            res.append("\nEmotion: " + emotion.getType() + "; confidence=" + emotion.getConfidence());
        }

        Eyeglasses eyeglasses = faceDetail.getEyeglasses();
        System.out.println("Eyeglasses: " + eyeglasses.getValue() + "; confidence=" + eyeglasses.getConfidence());
        res.append("\nEyeglasses: " + eyeglasses.getValue() + "; confidence=" + eyeglasses.getConfidence());
        EyeOpen eyesOpen = faceDetail.getEyesOpen();
        System.out.println("EyeOpen: " + eyesOpen.getValue() + "; confidence=" + eyesOpen.getConfidence());
        res.append("EyeOpen: " + eyesOpen.getValue() + "; confidence=" + eyesOpen.getConfidence());
        Gender gender = faceDetail.getGender();
        System.out.println("Gender: " + gender.getValue() + "; confidence=" + gender.getConfidence());
        res.append("\nGender: " + gender.getValue() + "; confidence=" + gender.getConfidence());
        List<Landmark> landmarks = faceDetail.getLandmarks();
        for (Landmark lm : landmarks) {
            System.out.println("Landmark: " + lm.getType() + ", x=" + lm.getX() + "; y=" + lm.getY());
            res.append("\nLandmark: " + lm.getType() + ", x=" + lm.getX() + "; y=" + lm.getY());
        }

        MouthOpen mouthOpen = faceDetail.getMouthOpen();
        System.out.println("MouthOpen: " + mouthOpen.getValue() + "; confidence=" + mouthOpen.getConfidence());
        res.append("\nMouthOpen: " + mouthOpen.getValue() + "; confidence=" + mouthOpen.getConfidence());
        Mustache mustache = faceDetail.getMustache();
        System.out.println("Mustache: " + mustache.getValue() + "; confidence=" + mustache.getConfidence());
        res.append("\nMustache: " + mustache.getValue() + "; confidence=" + mustache.getConfidence());
        Pose pose = faceDetail.getPose();
        System.out.println("Pose: pitch=" + pose.getPitch() + "; roll=" + pose.getRoll() + "; yaw" + pose.getYaw());
        res.append("\nPose: pitch=" + pose.getPitch() + "; roll=" + pose.getRoll() + "; yaw" + pose.getYaw());
        ImageQuality quality = faceDetail.getQuality();
        System.out.println("Quality: brightness=" + quality.getBrightness() + "; sharpness=" + quality.getSharpness());
        res.append("\nQuality: brightness=" + quality.getBrightness() + "; sharpness=" + quality.getSharpness());
        Smile smile = faceDetail.getSmile();
        System.out.println("Smile: " + smile.getValue() + "; confidence=" + smile.getConfidence());
        res.append("\nSmile: " + smile.getValue() + "; confidence=" + smile.getConfidence());
        Sunglasses sunglasses = faceDetail.getSunglasses();
        System.out.println("Sunglasses=" + sunglasses.getValue() + "; confidence=" + sunglasses.getConfidence());
        res.append("\nSunglasses=" + sunglasses.getValue() + "; confidence=" + sunglasses.getConfidence());
        System.out.println("###############");
        res.append("\n###############\n\n");
    }
}
