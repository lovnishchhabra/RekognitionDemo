package com.practice.aws;

public class App {

    public static void main(String[] args) throws InterruptedException {
    	args = new String[]{"list-collections","goodCollection","img\\kamal.jpg"};
    	if (args.length == 0) {
	    System.err.println("Please provide at least one argument.");
	    return;
	}
	switch (args[0]) {
	case "detect-labels":
	    DetectLabels detectLabels = new DetectLabels();
	    detectLabels.run(args);
	    break;
	case "detect-faces":
	    DetectFaces detectFaces = new DetectFaces();
	    detectFaces.run(args);
	    break;
	case "compare-faces":
            CompareFaces compareFaces = new CompareFaces();
            compareFaces.run(args);
            break;
	case "create-collection":
	    CreateCollection cc = new CreateCollection();
	    cc.run(args);
	    break;
	case "list-collections":
	    ListCollections lc = new ListCollections();
	    lc.run(args);
	    break;
	case "delete-collection":
            DeleteCollection dc = new DeleteCollection();
            dc.run(args);
            break;
	case "describe-collection":
            DescribeCollection descc = new DescribeCollection();
            descc.run(args);
            break;
	case "index-faces":
            IndexFaces indf = new IndexFaces();
            indf.run(args);
            break;
	case "search-faces-by-image":
            SearchFacesByImage sfbi = new SearchFacesByImage();
            sfbi.run(args);
            break;
	case "detect-labels-video":
            DetectLabelsVideo detectLabelsVideo = new DetectLabelsVideo();
            detectLabelsVideo.run(args);
            break;
	case "track-persons":
            TrackPersons trackPersons = new TrackPersons();
            trackPersons.run(args);
            break;
	default:
	    System.err.println("Unknown argument: " + args[0]);
	    return;
	}
    }
}
