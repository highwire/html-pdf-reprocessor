package com.htmltopdf.pdfservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "pdf_count")
@Entity
public class PdfCount  {

	@Id
	@Column(name = "count_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer countId;
	
	@Column(name = "count")
	private Integer count;
	
	@Column(name = "binsvc_url")
	private String binsvcUrl;

	@Column(name = "atom_path")
	private String atomPath;
	 

	 
	 

	public PdfCount(  Integer count, String binsvcUrl, String atomPath) {
		super();
		 
		this.count = count;
		this.binsvcUrl = binsvcUrl;
		this.atomPath = atomPath;
	}

	public String getBinsvcUrl() {
		return binsvcUrl;
	}

	public void setBinsvcUrl(String binsvcUrl) {
		this.binsvcUrl = binsvcUrl;
	}

	public String getAtomPath() {
		return atomPath;
	}

	public void setAtomPath(String atomPath) {
		this.atomPath = atomPath;
	}

	public Integer getCountId() {
		return countId;
	}

	public void setCountId(Integer countId) {
		this.countId = countId;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "PdfCount [countId=" + countId + ", count=" + count + ", binsvcUrl=" + binsvcUrl + ", atomPath="
				+ atomPath + "]";
	}

	public PdfCount() {
		 
	}

	 
 
	
	
	
}
