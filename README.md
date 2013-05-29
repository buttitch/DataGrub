DataGrub
========

A grub that eats data!

Running
--------

On *nix systems:

    ./DataGrub.groovy [args]

on others:

    groovy DataGrub.groovy [args]

Usage
--------


    usage: DataGrub.groovy [arguments]
    
     -d,--download            Download files that match the specified "tags"
                              and "formats" filter, if this option is not
                              specified the DataGrub just prints what files
                              match the specified "tags" and "formats" filter
                              to standard output.
     -f,--formats <formats>   What file formats are we after, defined by
                              "formats" comma separated list
     -h,--help                Show usage information
     -l,--list                list all available tags
     -o,--output <dir>        output directory to be used for downloaded
                              files, defined by "dir"
     -t,--tags <tags>         What datasets should be downloaded, defined by
                              "tags" comma separated list                          


Examples
--------

List available dataset "tags"

    ./DataGrub.groovy -l
    
Print to standard out all "shp" and "doc" files tagged "planning" in dataset

    ./DataGrub.groovy -f shp,doc -t planning
   
Download all "xlsx" files tagged "planning" to a "data" directory

    ./DataGrub.groovy -f xlsx -t planning -o data -d
    
License
-------

This work is released under the [GPL v3 license](http://www.gnu.org/licenses/gpl-3.0.txt)
    
