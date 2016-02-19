package org.dsrg.ourSpace.wea.domLogic;

public interface MessageConst {
	String ROLE_HAS_INSUFFICIENT_PRIVILEDGES_MSG = "Role %s has insufficient priviledges to perform %s";
	String EXPECTED_ATTRIBUTE_TYPE_MSG = "Expected %s attribute %s of type %s, was %s";
	String MANDATORY_ATTRIBUTE_MISSING_MSG = //
	"Mandatory %s attribute missing: %s"; // scope, attrName
	String EXPERIMENT_IS_ALREADY_FINISHED_MSG = "Experiment %d is already finished";
	String INVALID_ATTRIBUTE_VALUE_MSG = "Invalid %s attribute %s value '%s'";
	String INVALID_EXPERIMENT_ID_MSG = "Invalid experimentId %d: no such experiment";
	String INVALID_LOGIN_MSG = "Invalid user id or password";
	String FILL_OUT_REQUIRED_FIELDS = "Please fill out all required fields";
	String PLEASE_WAIT_FOR_PARTNER = "Please wait for your partner to catch up with you";
}
