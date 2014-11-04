package com.lumenare.dif.cli;

public class StaticPromptDetector implements PromptDetector {
        protected String prompt;

        public StaticPromptDetector(String staticPrompt) {
                prompt = staticPrompt;
        }

        // FIXME: prompt is immutable now.  we can remove the synch.

        public boolean lineIsPrompt(Line line) {
                synchronized(prompt) {
                        if(!(prompt.length() > 0)) { return(false); }

                        if(line.size() == prompt.length()) {
                                if(line.equals(prompt)) {
                                        return(true);
                                }
                        }
                }
                return(false);
        }
}
