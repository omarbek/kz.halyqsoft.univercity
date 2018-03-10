package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Feb 22, 2017 11:52:20 AM
 */
public final class VCurriculumCreditCycleSum implements Entity {

	private static final long serialVersionUID = 4059007400356061395L;
	
	private String cycleShortName;
	private String cycleName;
	private int totalCreditSum;
	private int requiredCreditSum;
	private int electiveCreditSum;
	
	public VCurriculumCreditCycleSum() {
	}

	public String getCycleShortName() {
		return cycleShortName;
	}

	public void setCycleShortName(String cycleShortName) {
		this.cycleShortName = cycleShortName;
	}

	public String getCycleName() {
		return cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}

	public int getTotalCreditSum() {
		return totalCreditSum;
	}

	public void setTotalCreditSum(int totalCreditSum) {
		this.totalCreditSum = totalCreditSum;
	}

	public int getRequiredCreditSum() {
		return requiredCreditSum;
	}

	public void setRequiredCreditSum(int requiredCreditSum) {
		this.requiredCreditSum = requiredCreditSum;
	}

	public int getElectiveCreditSum() {
		return electiveCreditSum;
	}

	public void setElectiveCreditSum(int electiveCreditSum) {
		this.electiveCreditSum = electiveCreditSum;
	}

	@Override
	public ID getId() {
		return null;
	}

	@Override
	public void setId(ID id) {
	}

	@Override
	public String logString() throws Exception {
		return null;
	}
}
