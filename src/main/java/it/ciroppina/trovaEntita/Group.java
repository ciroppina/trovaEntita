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

	public Group(String match) {
		this.match = match;
		this.count = 0L;
		this.offsets = new ArrayList<String>();
	}

	public Long getCount() {
		return this.count;
	}

	protected Long add(Long hm) {
		this.count += hm;
		return this.count;
	}

	public String getMatch() {
		return this.match;
	}

	protected List<String> addOffsets(int start, int end) {
		this.offsets.add(start+";"+end);
		return this.offsets;
	}

	public List<String> getOffsetList() {
		return this.offsets;
	}
}
