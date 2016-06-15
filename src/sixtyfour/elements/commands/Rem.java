package sixtyfour.elements.commands;

import sixtyfour.system.Machine;
import sixtyfour.system.ProgramCounter;

public class Rem extends AbstractCommand {

	public final static String REM_MARKER = "###";

	public Rem() {
		super("REM");
	}

	@Override
	public String parse(String linePart, int lineCnt, int lineNumber, int linePos, boolean lastPos, Machine memory) {
		super.parse(linePart, lineCnt, lineNumber, linePos, lastPos, memory);
		return REM_MARKER;
	}

	@Override
	public ProgramCounter execute(Machine memory) {
		return null;
	}
}
