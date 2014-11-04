package com.lumenare.dif.cli;

public class SimplePromptDetector implements PromptDetector {
	protected String[] _prompts;

	public SimplePromptDetector(String[] prompts) {
		_prompts = prompts;
		// TODO: sort (or hash) the list (by size or value) to efficientify the search
	}

	public boolean lineIsPrompt(Line line) {
		// TODO: fix bogosearch
		for(int i=0;i<_prompts.length;i++) {
			String prompt = _prompts[i];
			if(line.size() == prompt.length()) {
				if(line.equals(prompt)) {
					return(true);
				}
			}
		}
		return(false);
	}
}
