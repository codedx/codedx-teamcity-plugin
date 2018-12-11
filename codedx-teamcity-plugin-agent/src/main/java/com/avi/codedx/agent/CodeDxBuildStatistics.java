package com.avi.codedx.agent;

import com.avi.codedx.common.CodeDxConstants;
import com.codedx.client.model.GroupedCount;
import java.util.List;

public class CodeDxBuildStatistics {
	private List<GroupedCount> groupedSeverityCounts;
	private List<GroupedCount> groupedStatusCounts;

	public enum Group {
		SEVERITY,
		STATUS
	}

	public CodeDxBuildStatistics(List<GroupedCount> groupedSeverityCounts, List<GroupedCount> groupedStatusCounts) {
		this.groupedSeverityCounts = groupedSeverityCounts;
		this.groupedStatusCounts = groupedStatusCounts;
	}

	public List<GroupedCount> getGroupedSeverityCounts() {
		return groupedSeverityCounts;
	}

	public List<GroupedCount> getGroupedStatusCounts() {
		return groupedStatusCounts;
	}

	public static int getNumberOfFindingsForGroupAndName(final List<GroupedCount> groupedCounts, String name) {
		if(name.equals(CodeDxConstants.TOTAL)) {
			return getTotalFindingsForGroup(groupedCounts);
		}

		int count = 0;
		count = groupedCounts.stream()
			.filter(gc -> gc.getName().contains(name))
			.mapToInt(gc -> gc.getCount())
			.sum();

		return count;
	}

	public static int getTotalFindingsForGroup(List<GroupedCount> groupedCounts) {
		return groupedCounts.stream().mapToInt(gc -> gc.getCount()).sum();
	}

}
