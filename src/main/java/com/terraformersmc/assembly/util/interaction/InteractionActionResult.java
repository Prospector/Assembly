package com.terraformersmc.assembly.util.interaction;

import net.minecraft.util.ActionResult;

public enum InteractionActionResult {

	SUCCESS(ActionResult.SUCCESS),
	CONSUME(ActionResult.CONSUME),
	PASS(ActionResult.PASS, false),
	FAIL(ActionResult.FAIL),
	FORCE_PASS(ActionResult.PASS);

	ActionResult result;
	boolean cancel;

	InteractionActionResult(ActionResult result, boolean cancel) {
		this.result = result;
		this.cancel = cancel;
	}

	InteractionActionResult(ActionResult result) {
		this(result, true);
	}

	public ActionResult getActionResult() {
		return result;
	}

	public boolean cancels() {
		return cancel;
	}
}
