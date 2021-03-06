package com.sixtyfour.elements.commands;

import com.sixtyfour.elements.Type;
import com.sixtyfour.parser.Atom;
import com.sixtyfour.parser.Parser;
import com.sixtyfour.system.BasicProgramCounter;
import com.sixtyfour.system.Machine;
import com.sixtyfour.util.VarUtils;

import java.util.List;

/**
 * The PRINT# command.
 */
public class PrintFile extends Print {

	/** The file number. */
	private Atom fileNumber = null;

	/**
	 */
	public PrintFile() {
		super("PRINT#");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sixtyfour.elements.commands.Print#parse(java.lang.String, int, int,
	 * int, boolean, sixtyfour.system.Machine)
	 */
	@Override
	public String parse(String linePart, int lineCnt, int lineNumber, int linePos, boolean lastPos, Machine machine) {
		linePart = linePart.substring(this.name.length());
		int pos = linePart.indexOf(',');
		if (pos == -1) {
			pos = linePart.length();
		}
		term = Parser.getTerm(linePart.substring(0, pos), machine, false, true);
		linePart = pos != linePart.length() ? linePart.substring(pos + 1) : "";
		List<Atom> pars = Parser.getParameters(term);
		if (pars.size() != 1) {
			throw new RuntimeException("Syntax error: " + this);
		}
		fileNumber = pars.get(0);
		if (fileNumber.getType().equals(Type.STRING)) {
			throw new RuntimeException("Type mismatch error: " + this);
		}
		super.parse("PRINT" + linePart, lineCnt, lineNumber, linePos, lastPos, machine);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sixtyfour.elements.commands.Print#execute(sixtyfour.system.Machine)
	 */
	@Override
	public BasicProgramCounter execute(Machine machine) {
		int fn = VarUtils.getInt(fileNumber.eval(machine));
		return execute(machine, machine.getDeviceProvider(), fn);
	}

}
