#!/usr/bin/env groovy

//---
// This code is released under the GPL v3 license (see: http://www.gnu.org/licenses/gpl-3.0.txt)
//---

package buttitch.datagrub

import java.text.SimpleDateFormat
import groovyx.net.http.*
@Grab(group='org.codehaus.groovy.modules.http-builder',
module='http-builder', version='0.5.2' )

/**
 * Quick and nasty script for grabbing data from data.sa.gov.au
 * 
 * TODO make generic - for now hardcoded to data.sa.gov.au
 * @author Marcin N-K (@buttitch)
 */
class DataGrub { 
	def base = "http://data.sa.gov.au/api/3/action/"
	def http = new HTTPBuilder(base)
	def config = [:]

	def DataGrub() {}
	
	def DataGrub(config) {
		this.config = config
	}

	def download() {
		download(config)
	}

	def download(config) {
		config?.tags.each { tag ->
			http.get(path: 'tag_show',
			         query: [id: tag]) { response, json ->
				if (response.status == 200 && json.success) {
					json.result.packages.each { pack ->
						pack.resources.each { resource ->
							if (!config.formats || config.formats.contains(resource.format.toLowerCase())) {
								if (config.download) {
									downloadFile(resource.url, config)
								} else {
									println resource.toString(2)
								}
							}
						}
					}
				} else {
					System.err.println("Request failed. HTTP: ${response.status} Error: ${json && json.error}")
				}
			}
		}
	}
	
	def list() {
		http.get(path: 'tag_list') { response, json ->
			if (response.status == 200 && json.success) {
				println json.result.toString(2)
			}
		}
	}

	
	//---
	// helper methods
	//---

	def downloadFile(address, config) {
		print "Downloading: ${address} "
		def file = new FileOutputStream(new File(getOutputDir(config), address.tokenize("/")[-1]))
		def out = new BufferedOutputStream(file)
		out << new URL(address).openStream()
		out.close()
		println "DONE"
	}
	
	def getOutputDir(config) {
		def dir = new File(config.output ?: ".")
		dir.mkdirs()
		return dir
	}
	
	
	//---
	// Main
	//---

	static main(args) {
		// command-line args
		def cli = new CliBuilder(usage: 'DataGrub.groovy [arguments]')
		cli.with {
			h longOpt: 'help', 'Show usage information'
			f longOpt: 'formats', args: 1, argName: 'formats', 'What file formats are we after, defined by "formats" comma separated list'
			l longOpt: 'list',   'list all available tags'
			o longOpt: 'output', args: 1, argName: 'dir', 'output directory to be used for downloaded files, defined by "dir"'
			t longOpt: 'tags',  args: 1, argName: 'tags', 'What datasets should be downloaded, defined by "tags" comma separated list'
			d longOpt: 'download', 'Download files that match the specified "tags" and "formats" filter, if this option is not specified the DataGrub just prints what files match the specified "tags" and "formats" filter to standard output.' 
		}
		def options = cli.parse(args)
		if (!options || options.h) {
			cli.usage()
			return
		}

		// determine config
		def config = [download: false]
		if (options.formats) {  
			def formats = options.formats.toLowerCase().split(/\s*,\s*/)?.toList().toSet()
			config.put('formats', formats)
		}  
		if (options.output) {  
			config.put('output', options.output)
		}
		if (options.tags) {
			def tags = options.tags.split(/\s*,\s*/)?.toList()
			config.put('tags', tags)
		}
		if (options.download) {
			config.put('download', true)
		}
		
		println "DataGrub - Using config: ${config}"
		
		// do the work
		def grub = new DataGrub(config)
		if (options.list) {
			grub.list()
		}
		grub.download()
	}
}

