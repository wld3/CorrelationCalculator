package edu.umich.wld;

import java.util.UUID;

public class FileData {
	
	private String name = "(none)";
	private UUID requestId = null;
	private Boolean samplesInRows = true;
	private Boolean dataParsed = false;
	private Boolean dataNonPos = false;
	private Boolean didTransform = false;
	private Boolean didScaling = false;
	private Boolean didPearsonCoeff = false;
	private Boolean didPearsonHeatmap = false;
	private Boolean didPearsonDist = false;
	private Boolean didPearsonCluster = false;
	private Boolean didPearsonTreecut = false;
	private Boolean didPartial = false;
	private Integer nSamples = null;
	private Integer nMetabolites = null;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

	public UUID getRequestId() {
		return requestId;
	}
	
	public void setDataParsed(Boolean dataParsed) {
		this.dataParsed = dataParsed;
	}

	public Boolean getDataParsed() {
		return dataParsed;
	}
	
	public void setDataNonPos(Boolean dataNonPos) {
		this.dataNonPos = dataNonPos;
	}

	public Boolean getDataNonPos() {
		return dataNonPos;
	}
	
	public void setSamplesInRows(Boolean samplesInRows) {
		this.samplesInRows = samplesInRows;
	}

	public Boolean getSamplesInRows() {
		return samplesInRows;
	}
	
	public void setDidTransform(Boolean didTransform) {
		this.didTransform = didTransform;
	}

	public Boolean getDidTransform() {
		return didTransform;
	}
	
	public void setDidScaling(Boolean didScaling) {
		this.didScaling = didScaling;
	}

	public Boolean getDidScaling() {
		return didScaling;
	}
	
	public void setDidPearsonCoeff(Boolean didPearsonCoeff) {
		this.didPearsonCoeff = didPearsonCoeff;
	}

	public Boolean getDidPearsonCoeff() {
		return didPearsonCoeff;
	}
	
	public void setDidPearsonDist(Boolean didPearsonDist) {
		this.didPearsonDist = didPearsonDist;
	}

	public Boolean getDidPearsonDist() {
		return didPearsonDist;
	}
	
	public void setDidPearsonHeatmap(Boolean didPearsonHeatmap) {
		this.didPearsonHeatmap = didPearsonHeatmap;
	}

	public Boolean getDidPearsonHeatmap() {
		return didPearsonHeatmap;
	}
	
	public void setDidPearsonCluster(Boolean didPearsonCluster) {
		this.didPearsonCluster = didPearsonCluster;
	}

	public Boolean getDidPearsonCluster() {
		return didPearsonCluster;
	}
	
	public void setDidPearsonTreecut(Boolean didPearsonTreecut) {
		this.didPearsonTreecut = didPearsonTreecut;
	}

	public Boolean getDidPearsonTreecut() {
		return didPearsonTreecut;
	}
	
	public void setDidPartial(Boolean didPartial) {
		this.didPartial = didPartial;
	}

	public Boolean getDidPartial() {
		return didPartial;
	}
	
	public void setNSamples(Integer nSamples) {
		this.nSamples = nSamples;
	}

	public Integer getNSamples() {
		return nSamples;
	}
	
	public void setNMetabolites(Integer nMetabolites) {
		this.nMetabolites = nMetabolites;
	}

	public Integer getNMetabolites() {
		return nMetabolites;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((samplesInRows == null) ? 0 : samplesInRows.hashCode());
		result = prime * result + ((didTransform == null) ? 0 : didTransform.hashCode());
		result = prime * result + ((didScaling == null) ? 0 : didScaling.hashCode());
		result = prime * result + ((didPearsonCoeff == null) ? 0 : didPearsonCoeff.hashCode());
		result = prime * result + ((didPearsonHeatmap == null) ? 0 : didPearsonHeatmap.hashCode());
		result = prime * result + ((didPearsonCluster == null) ? 0 : didPearsonCluster.hashCode());
		result = prime * result + ((didPartial == null) ? 0 : didPartial.hashCode());
		result = prime * result + ((nSamples == null) ? 0 : nSamples.hashCode());
		result = prime * result + ((nMetabolites == null) ? 0 : nMetabolites.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileData other = (FileData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (samplesInRows == null) {
			if (other.samplesInRows != null)
				return false;
		} else if (!samplesInRows.equals(other.samplesInRows))
			return false;
		if (didTransform == null) {
			if (other.didTransform != null)
				return false;
		} else if (!didTransform.equals(other.didTransform))
			return false;
		if (didScaling == null) {
			if (other.didScaling != null)
				return false;
		} else if (!didScaling.equals(other.didScaling))
			return false;
		if (didPearsonCoeff == null) {
			if (other.didPearsonCoeff != null)
				return false;
		} else if (!didPearsonCoeff.equals(other.didPearsonCoeff))
			return false;
		if (didPearsonHeatmap == null) {
			if (other.didPearsonHeatmap != null)
				return false;
		} else if (!didPearsonHeatmap.equals(other.didPearsonHeatmap))
			return false;
		if (didPearsonCluster == null) {
			if (other.didPearsonCluster != null)
				return false;
		} else if (!didPearsonCluster.equals(other.didPearsonCluster))
			return false;
		if (didPartial == null) {
			if (other.didPartial != null)
				return false;
		} else if (!didPartial.equals(other.didPartial))
			return false;
		if (nSamples == null) {
			if (other.nSamples != null)
				return false;
		} else if (!nSamples.equals(other.nSamples))
			return false;
		if (nMetabolites == null) {
			if (other.nMetabolites != null)
				return false;
		} else if (!nMetabolites.equals(other.nMetabolites))
			return false;
		return true;
	}
	
	public String toString() {
		return name;
	}
	
}
