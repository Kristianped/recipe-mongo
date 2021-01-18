package no.kristianped.recipemongo.converters;

import no.kristianped.recipemongo.commands.NotesCommand;
import no.kristianped.recipemongo.domain.Notes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotesToNotesCommandTest {

    private static final String ID_VALUE = "1";
    private static final String DESCRIPTION = "Notes";

    NotesToNotesCommand converter;

    @BeforeEach
    void setUp() {
        converter = new NotesToNotesCommand();
    }

    @Test
    void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    void testEmptyObject() {
        assertNotNull(converter.convert(new Notes()));
    }

    @Test
    void convert() {
        // given
        Notes notes = new Notes();
        notes.setId(ID_VALUE);
        notes.setRecipeNotes(DESCRIPTION);

        // when
        NotesCommand notesCommand = converter.convert(notes);

        // then
        assertNotNull(notesCommand);
        assertEquals(ID_VALUE, notesCommand.getId());
        assertEquals(DESCRIPTION, notesCommand.getRecipeNotes());
    }
}