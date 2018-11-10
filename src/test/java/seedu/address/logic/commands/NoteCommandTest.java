package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_TEXT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_TEXT_WITH_FULL_STOP;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.updatePersonInModelWithNote;
import static seedu.address.logic.commands.NoteCommand.MESSAGE_SUCCESS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_LARGE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code NoteCommand}.
 */
public class NoteCommandTest {

    private static final String INVALID_TEXT_WITH_WHITESPACE_PREFIX = " Test.";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    private ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Rule
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void constructor_nullText_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new NoteCommand(INDEX_FIRST_PERSON, null);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new NoteCommand(null, VALID_NOTE_TEXT);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        try {
            new NoteCommand(INDEX_FIRST_PERSON, VALID_NOTE_TEXT).execute(null, commandHistory);
        } catch (CommandException e) {
            throw new AssertionError("CommandException should not be thrown.");
        }
    }

    @Test
    public void execute_addNote_success() {
        // check add successful to empty note
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String expectedMessage = String.format(MESSAGE_SUCCESS, personToEdit);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        updatePersonInModelWithNote(personToEdit, expectedModel, VALID_NOTE_TEXT_WITH_FULL_STOP);
        expectedModel.commitAddressBook();

        Command command = new NoteCommand(INDEX_FIRST_PERSON, VALID_NOTE_TEXT);
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);

        // undo test
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo test
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // check add successful to existing note
        Person newPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        updatePersonInModelWithNote(newPerson, expectedModel, VALID_NOTE_TEXT + ", " + VALID_NOTE_TEXT_WITH_FULL_STOP);
        expectedModel.commitAddressBook();

        Command secondCommand = new NoteCommand(INDEX_FIRST_PERSON, VALID_NOTE_TEXT);
        assertCommandSuccess(secondCommand, model, commandHistory, expectedMessage, expectedModel);

        // undo test
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo test
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        new NoteCommand(INDEX_LARGE, VALID_NOTE_TEXT).execute(model, commandHistory);
    }

    @Test
    public void execute_invalidText_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        new NoteCommand(INDEX_FIRST_PERSON, INVALID_TEXT_WITH_WHITESPACE_PREFIX).execute(model, commandHistory);
    }

    @Test
    public void equals() {
        NoteCommand sampleNoteCommand = new NoteCommand(INDEX_FIRST_PERSON, VALID_NOTE_TEXT_WITH_FULL_STOP);
        NoteCommand secondIndexNoteCommand = new NoteCommand(INDEX_SECOND_PERSON, VALID_NOTE_TEXT_WITH_FULL_STOP);
        NoteCommand noFullStopNoteCommand = new NoteCommand(INDEX_FIRST_PERSON, VALID_NOTE_TEXT);

        // same object -> returns true
        assertTrue(sampleNoteCommand.equals(sampleNoteCommand));

        // same values -> returns true
        NoteCommand sampleNoteCommandCopy = new NoteCommand(INDEX_FIRST_PERSON, VALID_NOTE_TEXT_WITH_FULL_STOP);
        assertTrue(sampleNoteCommand.equals(sampleNoteCommandCopy));

        // different types -> returns false
        assertFalse(sampleNoteCommand.equals(1));

        // null -> returns false
        assertFalse(sampleNoteCommand.equals(null));

        // different index -> returns false
        assertFalse(sampleNoteCommand.equals(secondIndexNoteCommand));

        // different text -> returns false
        assertFalse(sampleNoteCommand.equals(noFullStopNoteCommand));
    }
}
