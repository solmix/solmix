package org.solmix.runtime.transaction.proxy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.solmix.runtime.transaction.TransactionPolicy;

public class RuledTransactionMeta extends DefaultTransactionMeta {

	private static final long serialVersionUID = 6572528417422784367L;

	public static final String PREFIX_ROLLBACK_RULE = "-";

	/** Prefix for commit-on-exception rules in description strings */
	public static final String PREFIX_COMMIT_RULE = "+";


	/** Static for optimal serializability */
	private static final Log logger = LogFactory.getLog(RuledTransactionMeta.class);

	private List<RollbackRule> rollbackRules;
	public RuledTransactionMeta() {
		super();
	}

	public RuledTransactionMeta(RuledTransactionMeta other) {
		super(other);
		this.rollbackRules = new ArrayList<RollbackRule>(other.rollbackRules);
	}
	
	public RuledTransactionMeta(TransactionPolicy propagationBehavior, List<RollbackRule> rollbackRules) {
		super(propagationBehavior);
		this.rollbackRules = rollbackRules;
	}
	public void setRollbackRules(List<RollbackRule> rollbackRules) {
		this.rollbackRules = rollbackRules;
	}

	/**
	 * Return the list of {@code RollbackRule} objects
	 * (never {@code null}).
	 */
	public List<RollbackRule> getRollbackRules() {
		if (this.rollbackRules == null) {
			this.rollbackRules = new LinkedList<RollbackRule>();
		}
		return this.rollbackRules;
	}


	/**
	 * Winning rule is the shallowest rule (that is, the closest in the
	 * inheritance hierarchy to the exception). If no rule applies (-1),
	 * return false.
	 */
	@Override
	public boolean rollbackOn(Throwable ex) {
		if (logger.isTraceEnabled()) {
			logger.trace("Applying rules to determine whether transaction should rollback on " + ex);
		}

		RollbackRule winner = null;
		int deepest = Integer.MAX_VALUE;

		if (this.rollbackRules != null) {
			for (RollbackRule rule : this.rollbackRules) {
				int depth = rule.getDepth(ex);
				if (depth >= 0 && depth < deepest) {
					deepest = depth;
					winner = rule;
				}
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Winning rollback rule is: " + winner);
		}

		// User superclass behavior (rollback on unchecked) if no rule matches.
		if (winner == null) {
			logger.trace("No relevant rollback rule found: applying default rules");
			return super.rollbackOn(ex);
		}

		return !(winner instanceof NotRollbackRule);
	}




}
