package com.avi.codedx;

import io.swagger.client.model.GroupedCount;
import java.util.List;

public class CodeDxBuildStatistics {
	private List<GroupedCount> groupedCounts;

	public CodeDxBuildStatistics(List<GroupedCount> groupedSeverityCounts, List<GroupedCount> groupedStatusCounts) {
		this.groupedCounts = groupedSeverityCounts;
		groupedCounts.addAll(groupedStatusCounts);
	}

	public int getNumberOfFindingsForName(final String name) {
		GroupedCount groupedCount = groupedCounts.stream()
			.filter(gc -> gc.getName().equals(name))
			.findFirst()
			.orElse(null);

		return groupedCount == null ? 0 : groupedCount.getCount();
	}

}
