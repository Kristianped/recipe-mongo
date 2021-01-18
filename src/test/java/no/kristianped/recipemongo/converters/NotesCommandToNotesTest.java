package no.kristianped.recipemongo.converters;

import no.kristianped.recipemongo.commands.NotesCommand;
import no.kristianped.recipemongo.domain.Notes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotesCommandToNotesTest {

    private static final String ID_VALUE = "1";
    private static final String DESCRIPTION = "Notes";

    NotesCommandToNotes converter;

    @BeforeEach
    void setUp() {
        converter = new NotesCommandToNotes();
    }

    @Test
    void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    void testEmptyObject() {
        assertNotNull(converter.convert(new NotesCommand()));
    }

    @Test
    void convert() {
        // given
        NotesCommand notesCommand = new NotesCommand();
        notesCommand.setRecipeNotes(DESCRIPTION);
        notesCommand.setId(ID_VALUE);

        // when
        Notes notes = converter.convert(notesCommand);

        // then
        assertNotNull(notes);
        assertEquals(ID_VALUE, notes.getId());
        assertEquals(DESCRIPTION, notes.getRecipeNotes());
    }
}