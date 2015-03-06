package org.solmix.fmk.criterion;

import org.solmix.api.types.ValueEnum;

public enum Operator implements ValueEnum{
	/**
     * exactly equal to
     */
    EQUALS("equals"),
    /**
     * not equal to
     */
    NOT_EQUAL("notEqual"),
    /**
     * exactly equal to, if case is disregarded
     */
    IEQUALS("iEquals"),
    /**
     * not equal to, if case is disregarded
     */
    INOT_EQUAL("iNotEqual"),
    /**
     * Greater than
     */
    GREATER_THAN("greaterThan"),
    /**
     * Less than
     */
    LESS_THAN("lessThan"),
    /**
     * Greater than or equal to
     */
    GREATER_OR_EQUAL("greaterOrEqual"),
    /**
     * Less than or equal to
     */
    LESS_OR_EQUAL("lessOrEqual"),
    /**
     * Contains as sub-string (match case)
     */
    CONTAINS("contains"),
    /**
     * Starts with (match case)
     */
    STARTS_WITH("startsWith"),
    /**
     * Ends with (match case)
     */
    ENDS_WITH("endsWith"),
    /**
     * Contains as sub-string (case insensitive)
     */
    ICONTAINS("iContains"),
    /**
     * Starts with (case insensitive)
     */
    ISTARTS_WITH("iStartsWith"),
    /**
     * Ends with (case insensitive)
     */
    IENDS_WITH("iEndsWith"),
    /**
     * Does not contain as sub-string (match case)
     */
    NOT_CONTAINS("notContains"),
    /**
     * Does not start with (match case)
     */
    NOT_STARTS_WITH("notStartsWith"),
    /**
     * Does not end with (match case)
     */
    NOT_ENDS_WITH("notEndsWith"),
    /**
     * Does not contain as sub-string (case insensitive)
     */
    INOT_CONTAINS("iNotContains"),
    /**
     * Does not start with (case insensitive)
     */
    INOT_STARTS_WITH("iNotStartsWith"),
    /**
     * Does not end with (case insensitive)
     */
    INOT_ENDS_WITH("iNotEndsWith"),
    /**
     * shortcut for "greaterOrEqual" + "and" + "lessOrEqual" (case insensitive)
     */
    IBETWEEN_INCLUSIVE("iBetweenInclusive"),
    /**
     * Basic GLOB matching using wildcards (see {@link com.smartgwt.client.data.DataSource#getTranslatePatternOperators
     * translatePatternOperators} for more information on available patterns)
     */
    MATCHES_PATTERN("matchesPattern"),
    /**
     * Basic GLOB matching using wildcards (case insensitive) (see {@link
     * com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for more information on
     * available patterns)
     */
    IMATCHES_PATTERN("iMatchesPattern"),
    /**
     * GLOB matching using wildcards. Value is considered to meet the criterion if it contains the pattern. See {@link
     * com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for more information on
     * available patterns)
     */
    CONTAINS_PATTERN("containsPattern"),
    /**
     * GLOB mathcing using wildcards. Value is considered to meet the criterion if it starts with the pattern.See {@link
     * com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for more information on
     * available patterns)
     */
    STARTS_WITH_PATTERN("startsWithPattern"),
    /**
     * GLOB mathcing using wildcards. Value is considered to meet the criterion if it starts with the pattern.See {@link
     * com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for more information on
     * available patterns)
     */
    ENDS_WITH_PATTERN("endsWithPattern"),
    /**
     * GLOB matching using wildcards. Value is considered to meet the criterion if it contains the pattern. Matching is case
     * insensitive. See {@link com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for
     * more information on available patterns)
     */
    ICONTAINS_PATTERN("iContainsPattern"),
    /**
     * GLOB matching using wildcards. Value is considered to meet the criterion if it starts with the pattern. Matching is case
     * insensitive.See  {@link com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for
     * more information on available patterns)
     */
    ISTARTS_WITH_PATTERN("iStartsWithPattern"),
    /**
     * GLOB matching using wildcards.Value is considered to meet the criterion if it ends with the pattern. Matching is case
     * insensitive. See  {@link com.smartgwt.client.data.DataSource#getTranslatePatternOperators translatePatternOperators} for
     * more information on available patterns)
     */
    IENDS_WITH_PATTERN("iEndsWithPattern"),
    /**
     * Regular expression match
     */
    REGEXP("regexp"),
    /**
     * Regular expression match (case insensitive)
     */
    IREGEXP("iregexp"),
    /**
     * value is null
     */
    IS_NULL("isNull"),
    /**
     * value is non-null. Note empty string ("") is non-null
     */
    NOT_NULL("notNull"),
    /**
     * value is in a set of values. Specify criterion.value as an Array
     */
    IN_SET("inSet"),
    /**
     * value is not in a set of values. Specify criterion.value as an Array
     */
    NOT_IN_SET("notInSet"),
    /**
     * matches another field (match case, specify fieldName as criterion.value)
     */
    EQUALS_FIELD("equalsField"),
    /**
     * does not match another field (match case, specify fieldName as criterion.value)
     */
    NOT_EQUAL_FIELD("notEqualField"),
    /**
     * matches another field (case insensitive, specify fieldName as criterion.value)
     */
    IEQUALS_FIELD("iEqualsField"),
    /**
     * does not match another field (case insensitive, specify fieldName as criterion.value)
     */
    INOT_EQUAL_FIELD("iNotEqualField"),
    /**
     * Greater than another field (specify fieldName as criterion.value)
     */
    GREATER_THAN_FIELD("greaterThanField"),
    /**
     * Less than another field (specify fieldName as criterion.value)
     */
    LESS_THAN_FIELD("lessThanField"),
    /**
     * Greater than or equal to another field (specify fieldName as criterion.value)
     */
    GREATER_OR_EQUAL_FIELD("greaterOrEqualField"),
    /**
     * Less than or equal to another field (specify fieldName as criterion.value)
     */
    LESS_OR_EQUAL_FIELD("lessOrEqualField"),
    /**
     * Contains as sub-string (match case) another field value (specify fieldName as criterion.value)
     */
    CONTAINS_FIELD("containsField"),
    /**
     * Starts with (match case) another field value (specify fieldName as criterion.value)
     */
    STARTS_WITH_FIELD("startsWithField"),
    /**
     * Ends with (match case) another field value (specify fieldName as criterion.value)
     */
    ENDS_WITH_FIELD("endsWithField"),
    /**
     * Contains as sub-string (case insensitive) another field value (specify fieldName as criterion.value)
     */
    ICONTAINS_FIELD("iContainsField"),
    /**
     * Starts with (case insensitive) another field value (specify fieldName as criterion.value)
     */
    ISTARTS_WITH_FIELD("iStartsWithField"),
    /**
     * Ends with (case insensitive) another field value (specify fieldName as criterion.value)
     */
    IENDS_WITH_FIELD("iEndsWithField"),
    /**
     * Does not contain as sub-string (match case) another field value (specify fieldName as criterion.value)
     */
    NOT_CONTAINS_FIELD("notContainsField"),
    /**
     * Does not start with (match case) another field value (specify fieldName as criterion.value)
     */
    NOT_STARTS_WITH_FIELD("notStartsWithField"),
    /**
     * Does not end with (match case) another field value (specify fieldName as criterion.value)
     */
    NOT_ENDS_WITH_FIELD("notEndsWithField"),
    /**
     * Does not contain as sub-string (case insensitive) another field value (specify fieldName as criterion.value)
     */
    INOT_CONTAINS_FIELD("iNotContainsField"),
    /**
     * Does not start with (case insensitive) another field value (specify fieldName as criterion.value)
     */
    INOT_STARTS_WITH_FIELD("iNotStartsWithField"),
    /**
     * Does not end with (case insensitive) another field value (specify fieldName as criterion.value)
     */
    INOT_ENDS_WITH_FIELD("iNotEndsWithField"),
    /**
     * all subcriteria (criterion.criteria) are true
     */
    AND("and"),
    /**
     * all subcriteria (criterion.criteria) are false
     */
    NOT("not"),
    /**
     * at least one subcriteria (criterion.criteria) is true
     */
    OR("or"),
    /**
     * shortcut for "greaterThan" + "lessThan" + "and". Specify criterion.start and criterion.end
     */
    BETWEEN("between"),
    /**
     * shortcut for "greaterOrEqual" + "lessOrEqual" + "and". Specify criterion.start and criterion.end
     */
    BETWEEN_INCLUSIVE("betweenInclusive");
    
    private String value;

    Operator(String value) {
        this.value = value;
    }

 public static Operator fromValue(String v){
	 for (Operator c: Operator.values()) {
         if (c.value.equals(v)) {
             return c;
         }
     }
     throw new IllegalArgumentException(v);
 }

	@Override
	public String value() {
		return this.value;
	}
}
