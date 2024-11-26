package org.highwire.htmlpdf.reprocessor.repository;

import org.highwire.htmlpdf.reprocessor.entity.PdfCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HtmlPdfRepository extends JpaRepository<PdfCount,  Integer>{

 

	@Query("Select p from PdfCount p  WHERE p.atomPath= :atomPath")
	PdfCount findByAtomPath(@Param("atomPath") String atomPath);

	
	
}
