package com.rc;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;

import org.deeplearning4j.datasets.fetchers.MnistDataFetcher;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Autoencode {

	private static Logger log = LoggerFactory.getLogger(Autoencode.class);

	public static void main(String[] args) throws Exception {
		
//		Nd4j.ENFORCE_NUMERICAL_STABILITY = true ;

		int numSamples = MnistDataFetcher.NUM_EXAMPLES;
		int batchSize = 100 ;
		int imageSize = 28 * 28 ;
		int layers[] = { imageSize, 1000, 500, 250, 30  } ;
		
		Path target = Paths.get( "data" ) ;
		if( !Files.exists(target) ) {
			log.info( "Creating {}", target ) ;
			Files.createDirectories( target ) ;
		}

		DBNA dbna = new DBNA( target, layers ) ;
		WebServer webServer = new WebServer( dbna, layers ) ;
		
		log.info("Load datasets ... {} samples", numSamples );
		DataSetIterator iter = new MnistDataSetIterator( batchSize, numSamples, true ) ;
		
		dbna.train( iter ); 
		log.info("Saving params ...");
		
//		dbna.save( target );		
//		log.info("Web server in control ...");
	}
}
