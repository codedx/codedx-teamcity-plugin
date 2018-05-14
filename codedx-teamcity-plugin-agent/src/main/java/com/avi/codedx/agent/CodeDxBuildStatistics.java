package com.avi.codedx.agent;

import com.avi.codedx.common.CodeDxConstants;
import io.swagger.client.model.GroupedCount;
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

	public int getNumberOfFindingsForGroupAndName(final Group group, final String name) {
		if (name.equals(CodeDxConstants.TOTAL)) {
			return getTotalFindingsForGroup(group);
		}

		if (group == Group.SEVERITY){
			return getNumberOfFindingsForGroupAndName(groupedSeverityCounts, name);
		} else if (group == Group.STATUS) {
			return getNumberOfFindingsForGroupAndName(groupedStatusCounts, name);
		}
		return 0;
	}

	public int getNumberOfFindingsForGroupAndName(final List<GroupedCount> groupedCounts, String name) {
		GroupedCount groupedCount = groupedCounts.stream()
			.filter(gc -> gc.getName().equals(name))
			.findFirst()
			.orElse(null);

		return groupedCount == null ? 0 : groupedCount.getCount();
	}

	public int getTotalFindingsForGroup(Group group) {
		if (group == Group.SEVERITY) {
			return groupedSeverityCounts.stream().mapToInt(gc -> gc.getCount()).sum();
		} else if (group == Group.STATUS) {
			return groupedStatusCounts.stream().mapToInt(gc -> gc.getCount()).sum();
		}
		return 0;
	}

}
