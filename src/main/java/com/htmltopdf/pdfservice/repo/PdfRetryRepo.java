package com.htmltopdf.pdfservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.htmltopdf.pdfservice.model.PdfCount;

@Repository
public interface PdfRetryRepo extends JpaRepository<PdfCount,  Integer>{

 

	@Query("Select p from PdfCount p  WHERE p.atomPath= :atomPath")
	PdfCount findByAtomPath(@Param("atomPath") String atomPath);

	
	
}
