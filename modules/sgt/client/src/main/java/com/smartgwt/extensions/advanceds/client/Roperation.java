package com.smartgwt.extensions.advanceds.client;

import java.util.Map;

import com.smartgwt.client.core.DataClass;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.types.TextMatchStyle;
import com.smartgwt.client.util.EnumUtil;

public class Roperation extends DataClass {
	public Roperation() {

	}

	public String getAppID() {
		return getAttribute("appID");
	}

	/**
	 * Sets the value of the appID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAppID(String value) {
		setAttribute("appID", value);
	}

	/**
	 * Gets the value of the componentId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getComponentId() {
		return getAttribute("componentId");
	}

	/**
	 * Sets the value of the componentId property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setComponentId(String value) {
		setAttribute("componentId", value);
	}

	/**
	 * @return the operationId
	 */
	public Object getOperationId() {
		return getAttribute("operationId");
	}

	/**
	 * @param operationId
	 *            the operationId to set
	 */
	public void setOperationId(Object operationId) {
		setAttribute("operationId", operationId);
	}

	/**
	 * Gets the value of the outputs property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOutputs() {
		return getAttribute("outputs");
	}

	/**
	 * Sets the value of the outputs property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOutputs(String value) {
		setAttribute("outputs", value);
	}

	/**
	 * Gets the value of the startRow property.
	 * 
	 */
	public int getStartRow() {
		return getAttributeAsInt("startRow");
	}

	/**
	 * Sets the value of the startRow property.
	 * 
	 */
	public void setStartRow(int value) {
		setAttribute("startRow", value);
	}

	/**
	 * Gets the value of the endRow property.
	 * 
	 */
	public int getEndRow() {
		return getAttributeAsInt("endRow");
	}

	/**
	 * Sets the value of the endRow property.
	 * 
	 */
	public void setEndRow(int value) {
		setAttribute("endRow", value);
	}

	/**
	 * Gets the value of the sortBy property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the sortBy property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSortBy().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public SortSpecifier[] getSortBy() {
		return (SortSpecifier[]) getAttributeAsObject("sortBy");
	}

	/**
	 * @param sortBy
	 *            the sortBy to set
	 */
	public void setSortBy(SortSpecifier[] sortBy) {
		setAttribute("sortBy", sortBy);
	}

	/**
	 * Gets the value of the criteria property.
	 * 
	 * @return possible object is {@link Map<String,String> }
	 * 
	 */
	public Map<String, String> getCriteria() {
		return getAttributeAsMap("criteria");
	}

	/**
	 * Sets the value of the criteria property.
	 * 
	 * @param value
	 *            allowed object is {@link Map<String,String> }
	 * 
	 */
	public void setCriteria(Criteria criteria) {

		if (criteria != null) {
			setAttribute("criteria", criteria.getJsObj());
		}
	}

	/**
	 * Gets the value of the values property.
	 * 
	 * @return possible object is {@link Map<String,String> }
	 * 
	 */
	public Map<String, String> getValues() {
		return getAttributeAsMap("values");
	}

	/**
	 * Sets the value of the values property.
	 * 
	 * @param value
	 *            allowed object is {@link Map<String,String> }
	 * 
	 */
	public void setValues(Criteria value) {
		setAttribute("values", value);
	}

	/**
	 * Gets the value of the oldValues property.
	 * 
	 * @return possible object is {@link Map<String,String> }
	 * 
	 */
	public Map<String, String> getOldValues() {
		return getAttributeAsMap("oldValues");
	}

	/**
	 * Sets the value of the oldValues property.
	 * 
	 * @param value
	 *            allowed object is {@link Map<String,String> }
	 * 
	 */
	public void setOldValues(Map<String, String> value) {
		setAttribute("oldValues", value);
	}

	/**
	 * Gets the value of the textMatchStyle property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public TextMatchStyle getTextMatchStyle() {
		return EnumUtil.getEnum(TextMatchStyle.values(),
				getAttribute("textMatchStyle"));
	}

	/**
	 * Sets the value of the textMatchStyle property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTextMatchStyle(TextMatchStyle textMatchStyle) {
		setAttribute("textMatchStyle", textMatchStyle == null ? null
				: textMatchStyle.getValue());
	}

	/**
	 * Gets the value of the requestId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRequestId() {
		return getAttribute("requestId");
	}

	/**
	 * Sets the value of the requestId property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRequestId(String value) {
		setAttribute("requestId", value);
	}

	/**
	 * Gets the value of the exportResults property.
	 * 
	 */
	public boolean isExportResults() {
		return getAttributeAsBoolean("exportResults");
	}

	/**
	 * Sets the value of the exportResults property.
	 * 
	 */
	public void setExportResults(boolean value) {
		setAttribute("exportResults", value);
	}

	/**
	 * Gets the value of the exportAs property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public ExportFormat getExportAs() {
		return EnumUtil
				.getEnum(ExportFormat.values(), getAttribute("exportAs"));
	}

	/**
	 * Sets the value of the exportAs property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportAs(ExportFormat exportAs) {
		setAttribute("exportAs", exportAs == null ? null : exportAs.getValue());
	}

	/**
	 * Gets the value of the exportFilename property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExportFilename() {
		return getAttribute("exportFilename");
	}

	/**
	 * Sets the value of the exportFilename property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportFilename(String value) {
		setAttribute("exportFilename", value);
	}

	/**
	 * Gets the value of the lineBreakStyle property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLineBreakStyle() {
		return getAttribute("lineBreakStyle");
	}

	/**
	 * Sets the value of the lineBreakStyle property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLineBreakStyle(String value) {
		setAttribute("lineBreakStyle", value);
	}

	/**
	 * Gets the value of the exportDelimiter property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExportDelimiter() {
		return getAttribute("exportDelimiter");
	}

	/**
	 * Sets the value of the exportDelimiter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportDelimiter(String value) {
		setAttribute("exportDelimiter", value);
	}

	/**
	 * Gets the value of the exportTitleSeparatorChar property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExportTitleSeparatorChar() {
		return getAttribute("exportTitleSeparatorChar");
	}

	/**
	 * Sets the value of the exportTitleSeparatorChar property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportTitleSeparatorChar(String value) {
		setAttribute("exportTitleSeparatorChar", value);
	}

	/**
	 * Gets the value of the exportDisplay property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public ExportDisplay getExportDisplay() {
		return EnumUtil.getEnum(ExportDisplay.values(),
				getAttribute("exportDisplay"));
	}

	/**
	 * Sets the value of the exportDisplay property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportDisplay(ExportDisplay exportDisplay) {
		setAttribute("exportDisplay", exportDisplay == null ? null
				: exportDisplay.getValue());

	}

	/**
	 * Gets the value of the exportHeader property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExportHeader() {
		return getAttribute("exportHeader");
	}

	/**
	 * Sets the value of the exportHeader property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportHeader(String value) {
		setAttribute("exportHeader", value);
	}

	/**
	 * Gets the value of the exportFooter property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExportFooter() {
		return getAttribute("exportFooter");
	}

	/**
	 * Sets the value of the exportFooter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportFooter(String value) {
		setAttribute("exportFooter", value);
	}

	/**
	 * Gets the value of the exportFields property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String[] getExportFields() {
		return getAttributeAsStringArray("exportFields");
	}

	/**
	 * Sets the value of the exportFields property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExportFields(String[] value) {
		setAttribute("exportFields", value);
	}

	/**
	 * @return the dataSource
	 */
	public String getDataSource() {
		return getAttribute("dataSource");
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(String dataSource) {
		setAttribute("dataSource", dataSource);
	}

	/**
	 * @return the operationType
	 */
	public DSOperationType getOperationType() {
		return EnumUtil.getEnum(DSOperationType.values(),
				getAttribute("operationType"));
	}

	/**
	 * @param operationType
	 *            the operationType to set
	 */
	public void setOperationType(DSOperationType operationType) {
		setAttribute("operationType", operationType == null ? null
				: operationType.getValue());
	}

	/**
	 * @return the repo
	 */
	public String getRepo() {
		return getAttribute("repo");
	}

	/**
	 * @param repo
	 *            the repo to set
	 */
	public void setRepo(String repo) {
		setAttribute("repo", repo);
	}

	public void setExportDatesAsFormattedString(
			Boolean exportDatesAsFormattedString) {
		setAttribute("exportDatesAsFormattedString",
				exportDatesAsFormattedString);
	}
}
