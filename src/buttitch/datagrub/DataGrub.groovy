#!/usr/bin/env groovy

/**
 * Quick and nasty script for grabbing data from data.sa.gov.au
 *
 * @author Marcin N-K (@buttitch)
 */

package buttitch.datagrub

import java.text.SimpleDateFormat
import groovyx.net.http.*
@Grab(group='org.codehaus.groovy.modules.http-builder',
	module='http-builder', version='0.5.2' )

// TODO make generic - for now hardcoded to data.sa.gov.au
def base = "http://data.sa.gov.au/api/3/action/"
def isoTimestamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date())
 
def http = new HTTPBuilder(base)
 
http.get(path: 'tag_show',
	 query: [id: 'planning']) { response, json ->
 
	if (response.status == 200 && json.success) {
		json.result.packages.each { pack ->
			// TODO make generic - for now just download any resources in shapefile format
			pack.resources.each { resource ->
				if (resource.format.toLowerCase() == "shp") {
					download(resource.url)
				}
			}
		}
		
		// TODO remove this debug - for now dump json to file for eyeballing
		def dump = new File("query-${isoTimestamp}.json")
		dump << json.toString(2)
	} else {
		System.err.println("Request failed. HTTP: ${response.status} Error: ${json && json.error}")
	}
}
		
//---
// helper functions
//---
		
def download(address) {
	print "Downloading: ${address} "
	def file = new FileOutputStream(address.tokenize("/")[-1])
	def out = new BufferedOutputStream(file)
	out << new URL(address).openStream()
	out.close()
	println "DONE"
}

