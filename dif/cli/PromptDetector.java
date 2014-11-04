package com.lumenare.dif.cli;

public interface PromptDetector {
        public boolean lineIsPrompt(Line line);

        // TODO: Gosh, how else do I stick other methods in here I wonder.
        // TODO: Should there be a lineIsMorePrompt().
        // TODO: Or should we define detectLineType() which returns a LineType
        // TODO: and then LineType could enumerate PromptLine, More, ...

        // TODO: What about lineIsPrivilegedPrompt().
        // TODO: What about lineIsUnprivelegedPrompt().
}
