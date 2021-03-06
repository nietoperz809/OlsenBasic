package com.sixtyfour.elements.commands;

import com.sixtyfour.system.BasicProgramCounter;
import com.sixtyfour.system.Machine;

/**
 * The END command.
 */
public class End extends AbstractCommand {

	/**
	 * Instantiates a new end.
	 * 
	 * @param name
	 *            the name
	 */
	protected End(String name) {
		super(name);
	}

	/**
	 * Instantiates a new end.
	 */
	public End() {
		super("END");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sixtyfour.elements.commands.AbstractCommand#parse(java.lang.String,
	 * int, int, int, boolean, sixtyfour.system.Machine)
	 */
	@Override
	public String parse(String linePart, int lineCnt, int lineNumber, int linePos, boolean lastPos, Machine machine) {
		super.parse(linePart, lineCnt, lineNumber, linePos, lastPos, machine);
		if (linePart.trim().length() > 3) {
			throw new RuntimeException("Syntax error: " + this);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sixtyfour.elements.commands.AbstractCommand#execute(sixtyfour.system.
	 * Machine)
	 */
	@Override
	public BasicProgramCounter execute(Machine machine) {
		BasicProgramCounter pc = new BasicProgramCounter(this.lineCnt, this.linePos);
		pc.setEnd(true);
		return pc;
	}

}
