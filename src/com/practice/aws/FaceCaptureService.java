package com.practice.aws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class FaceCaptureService {

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/addCollection/{name}")
	public String addCollection (@PathParam("name") String name) {
		CreateCollection cc = new CreateCollection();
		String[] args = new String[]{"create-collection", name};
		return cc.run(args);
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/deleteCollection/{name}")
	public String deleteCollection (@PathParam("name") String name) {
		DeleteCollection cc = new DeleteCollection();
		String[] args = new String[]{"delete-collection", name};
		return cc.run(args);
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/describeCollection/{name}")
	public String describeCollection (@PathParam("name") String name) {
		DescribeCollection cc = new DescribeCollection();
		String[] args = new String[]{"describe-collection", name};
		return cc.run(args);
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/listCollections")
	public String listCollections () {
		ListCollections cc = new ListCollections();
		String[] args = new String[]{"list-collection"};
		return cc.run(args);
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/addImageToCollection/{collectionName}/{imageName}/{image}")
	public String addImageToCollection (@PathParam("collectionName") String collectionName,@PathParam("imageName") String imageName,
			@PathParam("image") byte[] image) {
		IndexFaces2 cc = new IndexFaces2();
		return cc.run(collectionName, imageName, image);
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/detectFace/{imageName}/{image}")
	public String detectFace (@PathParam("imageName") String imageName,
			@PathParam("image") byte[] image) {
		DetectFaces2 cc = new DetectFaces2();
		return cc.run(imageName, image);
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/compareFaces/{image1}/{image2}")
	public String compareFaces (@PathParam("image1") byte[] image1,
			@PathParam("image2") byte[] image2) {
		CompareFaces2 cc = new CompareFaces2();
		return cc.run(image1, image2);
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/searchFace/{collectionName}/{image}")
	public String compareFaces (@PathParam("collectionName") String collectionName,@PathParam("image") byte[] image) {
		SearchFacesByImage2 cc = new SearchFacesByImage2();
		return cc.run(collectionName, image);
	}
}
