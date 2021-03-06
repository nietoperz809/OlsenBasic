package com.sixtyfour.elements.commands;

import com.sixtyfour.elements.Type;
import com.sixtyfour.parser.Atom;
import com.sixtyfour.parser.Parser;
import com.sixtyfour.system.BasicProgramCounter;
import com.sixtyfour.system.Machine;
import com.sixtyfour.util.VarUtils;

import java.util.List;

/**
 * The CLOSE command.
 */
public class Close extends AbstractCommand {

	/** The pars. */
	private List<Atom> pars;

	/**
	 * Instantiates a new close.
	 */
	public Close() {
		super("CLOSE");
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
		term = Parser.getTerm(this, linePart, machine, true);
		pars = Parser.getParameters(term);

		if (pars.size() != 1) {
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
		Atom fileNumber = pars.get(0);
		if (fileNumber.getType().equals(Type.STRING)) {
			throw new RuntimeException("Type mismatch error: " + this);
		}
		int fn = VarUtils.getInt(fileNumber.eval(machine));

		if (machine.getOutputChannel().getPrintConsumer() != null) {
			if (machine.getOutputChannel().getChannel() == fn) {
				machine.getOutputChannel().setPrintConsumer(null, 0);
			}
		}

		machine.getDeviceProvider().close(fn);
		return null;
	}

}
