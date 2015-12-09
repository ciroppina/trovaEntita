package it.ciroppina.trovaEntita;

import java.util.ArrayList;
import java.util.List;

/**
 * this @class represents a Group, i.e. the occurrences of a String matched by a RegEx
 * @author ciroppina
 *
 */
public class Group {

	private String match = "";
	private Long count = 0L;
	private List<String> offsets;
	private String mainQualifier;

	public Group(String match) {
		this.match = match;
		this.count = 0L;
		this.offsets = new ArrayList<String>();
		this.mainQualifier = "UNDEFINED";
	}

	protected Long add(Long hm) {
		this.count += hm;
		return this.count;
	}

	protected List<String> addOffsets(int start, int end) {
		this.offsets.add(start+";"+end);
		return this.offsets;
	}

	protected void updateMainQualifier(String qualifier) {
		this.mainQualifier = qualifier;
	}
	
	protected void addMoreQualifiers(String moreQualifiers) {
		this.mainQualifier += ","+moreQualifiers;
	}

	public Long getCount() {
		return this.count;
	}

	public String getMatch() {
		return this.match;
	}

	public List<String> getOffsetList() {
		return this.offsets;
	}

	public String getMainQualifier() {
		return mainQualifier;
	}

}
