package ir.fileWork.builder;

import ir.structures.abstraction.InvertedIndex;
import ir.structures.impl.*;

public class BuilderFactory {

    public Builder getEditableBuilder(InvertedIndex index) throws IllegalStateException {
        if (index instanceof InvertedIndexGram || index instanceof InvertedIndexTree ||
                index instanceof InvertedIndexInversion)
            return new TermBuilder(index);
        else if (index instanceof InvertedIndexTermCoords)
            return new CoordBuider(index);
        else if (index instanceof InvertedIndexPhrase)
            return new PhraseBuilder(index);
        else
            throw new IllegalArgumentException("There is no builder for this index: " + index.getClass().getSimpleName());
    }
}
