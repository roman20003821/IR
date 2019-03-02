package ir.fileWork.builder;

import ir.structures.IndexType;
import ir.structures.abstraction.Editable;
import ir.structures.impl.InvertedIndexTermCoords;

public class BuilderFactory {

    private Editable editable;
    private InvertedIndexTermCoords editableWithCoords;

    public BuilderFactory(Editable editable) {
        this.editable = editable;
    }

    public BuilderFactory(InvertedIndexTermCoords editableWithCoords) {
        this.editableWithCoords = editableWithCoords;
    }

    public Builder getEditableBuilder(IndexType type) throws IllegalStateException {
        switch (type) {
            case TERM:
            case GRAM:
            case TREE:
            case MATRIX:
            case INVERSION:
                if (editable == null) throw new IllegalStateException("You must use another constructor");
                return new TermBuilder(editable);
            case TERM_WITH_COORDS:
                if (editableWithCoords == null) throw new IllegalStateException("You must use another constructor");
                return new CoordBuider(editableWithCoords);
            case PHRASE:
                if (editable == null) throw new IllegalStateException("You must use another constructor");
                return new PhraseBuilder(editable);
            default:
                return null;
        }
    }


}
