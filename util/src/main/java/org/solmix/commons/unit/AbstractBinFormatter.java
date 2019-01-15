
package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.solmix.commons.util.ArrayUtils;

public abstract class AbstractBinFormatter implements Formatter {

	private NumberFormat getSpecificFormatter(FormatSpecifics specifics, Locale locale) {
		NumberFormat res = NumberFormat.getInstance(locale);

		if (specifics.getPrecision() == FormatSpecifics.PRECISION_MAX) {
			res.setMaximumFractionDigits(100);
			res.setMinimumFractionDigits(0);
		} else {
			res.setMaximumFractionDigits(1);
			res.setMinimumFractionDigits(1);
		}

		return res;
	}

	@Override
	public FormattedNumber format(UnitNumber val, Locale locale, FormatSpecifics specifics) {
		NumberFormat fmt;
		BigDecimal baseVal;
		double newVal;
		int targScale;

		baseVal = val.getBaseValue();
		targScale = this.findGoodLookingScale(baseVal);
		newVal = this.getTargetValue(baseVal, targScale);

		if (specifics == null)
			fmt = UnitUtil.getNumberFormat(new double[] { newVal }, locale);
		else
			fmt = this.getSpecificFormatter(specifics, locale);

		return this.createFormattedValue(newVal, targScale, fmt);
	}

	@Override
	public FormattedNumber[] formatSame(double[] vals, int unitType, int scale, Locale locale,
			FormatSpecifics specifics) {
		FormattedNumber[] res;
		NumberFormat fmt;
		UnitNumber tmpNum;
		double[] newVals;
		double average;
		int targScale;

		res = new FormattedNumber[vals.length];

		if (vals.length == 0) {
			return res;
		}

		average = ArrayUtils.average(vals);

		tmpNum = new UnitNumber(average, unitType, scale);
		targScale = this.findGoodLookingScale(tmpNum.getBaseValue());

		newVals = new double[vals.length];
		for (int i = 0; i < vals.length; i++) {
			tmpNum = new UnitNumber(vals[i], unitType, scale);
			newVals[i] = this.getTargetValue(tmpNum.getBaseValue(), targScale);
		}

		if (specifics == null)
			fmt = UnitUtil.getNumberFormat(newVals, locale);
		else
			fmt = this.getSpecificFormatter(specifics, locale);

		for (int i = 0; i < vals.length; i++) {
			res[i] = this.createFormattedValue(newVals[i], targScale, fmt);
		}
		return res;
	}

	protected abstract String getTagName();

	protected FormattedNumber createFormattedValue(double value, int scale, NumberFormat fmt) {
		String tag;

		switch (scale) {
		case UnitConstants.SCALE_NONE:
			tag = "";
			break;
		case UnitConstants.SCALE_KILO:
			tag = "K";
			break;
		case UnitConstants.SCALE_MEGA:
			tag = "M";
			break;
		case UnitConstants.SCALE_GIGA:
			tag = "G";
			break;
		case UnitConstants.SCALE_TERA:
			tag = "T";
			break;
		case UnitConstants.SCALE_PETA:
			tag = "P";
			break;
		default:
			throw new IllegalStateException("Unhandled scale");
		}

		return new FormattedNumber(fmt.format(value), tag + this.getTagName());
	}

	private double getTargetValue(BigDecimal baseVal, int targetScale) {
		BigDecimal modifier;
		double lateModifier;

		modifier = UnitUtil.FACT_NONE;
		lateModifier = 1.0;

		switch (targetScale) {
		case UnitConstants.SCALE_KILO:
			lateModifier = 1 << 10;
			break;
		case UnitConstants.SCALE_MEGA:
			lateModifier = 1 << 20;
			break;
		case UnitConstants.SCALE_GIGA:
			modifier = UnitUtil.FACT_MEGA_BIN;
			lateModifier = 1 << 10;
			break;
		case UnitConstants.SCALE_TERA:
			modifier = UnitUtil.FACT_GIGA_BIN;
			lateModifier = 1 << 10;
			break;
		case UnitConstants.SCALE_PETA:
			modifier = UnitUtil.FACT_TERA_BIN;
			lateModifier = 1 << 10;
			break;
		}

		baseVal = baseVal.divide(modifier);
		return baseVal.doubleValue() / lateModifier;
	}

	private int findGoodLookingScale(BigDecimal val) {
		if (val.compareTo(UnitUtil.FACT_PETA_BIN) >= 1) {
			return UnitConstants.SCALE_PETA;
		} else if (val.compareTo(UnitUtil.FACT_TERA_BIN) >= 1) {
			return UnitConstants.SCALE_TERA;
		} else if (val.compareTo(UnitUtil.FACT_GIGA_BIN) >= 1) {
			return UnitConstants.SCALE_GIGA;
		} else if (val.compareTo(UnitUtil.FACT_MEGA_BIN) >= 1) {
			return UnitConstants.SCALE_MEGA;
		} else if (val.compareTo(UnitUtil.FACT_KILO_BIN) >= 1) {
			return UnitConstants.SCALE_KILO;
		} else {
			return UnitConstants.SCALE_NONE;
		}
	}

	@Override
	public BigDecimal getBaseValue(double value, int scale) {
		BigDecimal res;

		res = new BigDecimal(value);
		return res.multiply(this.getScaleCoeff(scale));
	}

	@Override
	public BigDecimal getScaledValue(BigDecimal value, int targScale) {
		return value.divide(this.getScaleCoeff(targScale));
	}

	private BigDecimal getScaleCoeff(int scale) {
		switch (scale) {
		case UnitConstants.SCALE_NONE:
			return UnitUtil.FACT_NONE;
		case UnitConstants.SCALE_BIT:
			return UnitUtil.FACT_BIT;
		case UnitConstants.SCALE_KILO:
			return UnitUtil.FACT_KILO_BIN;
		case UnitConstants.SCALE_MEGA:
			return UnitUtil.FACT_MEGA_BIN;
		case UnitConstants.SCALE_GIGA:
			return UnitUtil.FACT_GIGA_BIN;
		case UnitConstants.SCALE_TERA:
			return UnitUtil.FACT_TERA_BIN;
		case UnitConstants.SCALE_PETA:
			return UnitUtil.FACT_PETA_BIN;
		}

		throw new IllegalArgumentException("Value did not have binary " + "based scale");
	}

	protected abstract UnitNumber parseTag(double number, String tag, int tagIdx, ParseSpecifics specifics)
			throws ParseException;

	@Override
	public UnitNumber parse(String val, Locale locale, ParseSpecifics specifics) throws ParseException {
		NumberFormat fmt = NumberFormat.getInstance(locale);
		double numberPart;
		int nonIdx;

		nonIdx = UnitUtil.findNonNumberIdx(val, fmt);
		if (nonIdx == -1) {
			throw new ParseException("Number had no units with it", val.length());
		}

		if (nonIdx == 0) {
			throw new ParseException("Invalid number specified", 0);
		}

		numberPart = fmt.parse(val.substring(0, nonIdx)).doubleValue();
		return this.parseTag(numberPart, val.substring(nonIdx, val.length()).trim(), nonIdx, specifics);
	}

}
