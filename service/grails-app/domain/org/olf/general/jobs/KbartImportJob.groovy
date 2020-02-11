package org.olf.general.jobs

import grails.gorm.MultiTenant

import org.springframework.web.multipart.MultipartFile
import org.apache.commons.io.input.BOMInputStream

import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder

class KbartImportJob extends PersistentJob implements MultiTenant<KbartImportJob> {
  
  String packageName
  String packageSource
  String packageReference
  
  final Closure getWork() {
    
    final Closure theWork = { final String eventId, final String tenantId ->
    
      log.info "Running KBART Package Import Job"
      PersistentJob.withTransaction {
      
        // We should ensure the job is read into the current session. This closure will probably execute
        // in a future session and we need to reread the event in.
        final KbartImportJob job = KbartImportJob.read(eventId)

        Map packageInfo = [
          packageName: job.packageName,
          packageSource: job.packageSource,
          packageReference: job.packageReference
        ]

        //TODO Actual validation here
        log.debug("Check that the extra fields Map is correct: ${packageInfo}")
        

        if (job.fileUpload) {
           log.debug("Incoming Job Stream: ${job.fileUpload.fileObject.fileContents.binaryStream}")
          BOMInputStream bis = new BOMInputStream(job.fileUpload.fileObject.fileContents.binaryStream);
          Reader fr = new InputStreamReader(bis);
          CSVReader csvReader = new CSVReaderBuilder(fr).build();
          importService.importPackageFromKbart(csvReader, packageInfo)
        } else {
          log.error "No file attached to the Job."
        }
      }
    }.curry( this.id )
    
    theWork
  }
  
  void beforeValidate() {
    if (!this.name && this.fileUpload) {
      // Set the name from the file upload if no name has been set.
      this.name = "Import package from ${this.fileUpload.fileName}"
    }
  }
  
  static constraints = {
      fileUpload (nullable:false)
  }

  static mapping = {
                      version false
                 packageName column:'package_name'
               packageSource column:'package_source'
            packageReference column:'package_reference'
  }
}
