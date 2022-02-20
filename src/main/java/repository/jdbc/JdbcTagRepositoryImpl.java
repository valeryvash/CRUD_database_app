package repository.jdbc;

import model.Tag;
import repository.TagRepository;
import util.PreparedStatementProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTagRepositoryImpl implements TagRepository {

    private Tag getTagFromResultSet(ResultSet resultSet) throws SQLException {
        Tag t = new Tag();
        if (resultSet.next()) {
            t.setId(resultSet.getLong("id"));
            t.setTagName(resultSet.getString("name"));
            return t;
        }
        return t;
    }

    private List<Tag> getListOfTagsFromResultSet(ResultSet resultSet) throws SQLException {
        List<Tag> tags = new ArrayList<>();
        while (resultSet.next()) {
            Tag t = new Tag();
            t.setId(resultSet.getLong("id"));
            t.setTagName(resultSet.getString("name"));
            tags.add(t);
        }
        return tags;
    }

    @Override
    public Tag add(Tag entity) {
        long idToBeReturned = -1L;
        Tag tagToBeReturned;

        String addTagQuery = "INSERT INTO tags (name) VALUE (?);";
        try (PreparedStatement addTagStatement =
                     PreparedStatementProvider.prepareStatement(
                             addTagQuery,
                             PreparedStatement.RETURN_GENERATED_KEYS)) {
            addTagStatement.setString(1, entity.getTagName());
            addTagStatement.executeUpdate();

            ResultSet addedTagIdSet = addTagStatement.getGeneratedKeys();
            if (addedTagIdSet.next()) {
                idToBeReturned = addedTagIdSet.getLong("GENERATED_KEY");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tagToBeReturned = get(idToBeReturned);
        return tagToBeReturned;
    }

    @Override
    public Tag get(Long id) {
        Tag tagToBeReturned = new Tag();
        String getTagQuery = "SELECT id, name FROM tags WHERE id = ? ;";
        try (PreparedStatement getTagStatement =
                     PreparedStatementProvider.prepareStatement(getTagQuery)) {
            getTagStatement.setLong(1, id);
            ResultSet answerResultSet = getTagStatement.executeQuery();
            tagToBeReturned = this.getTagFromResultSet(answerResultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagToBeReturned;
    }

    @Override
    public Tag update(Tag entity) {
        long updatedTagId = entity.getId();
        Tag tagToBeReturned;

        String updateTagQuery =
                "UPDATE tags SET tags.name = ? " +
                        "WHERE tags.id = ? ;";
        try (PreparedStatement updateTagStatement =
                     PreparedStatementProvider.prepareStatement(updateTagQuery)) {
            updateTagStatement.setString(1, entity.getTagName());
            updateTagStatement.setLong(2, entity.getId());
            updateTagStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tagToBeReturned = get(updatedTagId);

        return tagToBeReturned;
    }

    @Override
    public void remove(Long id) {
        String deleteTagQuery = "DELETE FROM tags WHERE tags.id = ? ;";
        try (PreparedStatement deleteTagStatement =
                     PreparedStatementProvider.prepareStatement(deleteTagQuery);) {
            deleteTagStatement.setLong(1, id);
            deleteTagStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Tag> getAll() {
        List<Tag> tagsToBeReturned = new ArrayList<>();

        String getAllTagsQuery = "SELECT id, name FROM tags;";
        try (
                PreparedStatement getAllTagsStatement =
                        PreparedStatementProvider.prepareStatement(getAllTagsQuery);
                ResultSet answerToGetAllTagsStatement = getAllTagsStatement.executeQuery()) {
            tagsToBeReturned = this.getListOfTagsFromResultSet(answerToGetAllTagsStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagsToBeReturned;
    }

    @Override
    public Tag getByName(String name) {
        Tag tagToBeReturned = new Tag();
        String getTagByNameQuery =
                "SELECT id, name " +
                        "FROM tags " +
                        "WHERE name = ? ;";
        try (PreparedStatement getTagByNameStatement =
                     PreparedStatementProvider.prepareStatement(getTagByNameQuery)
        ) {
            getTagByNameStatement.setString(1, name);
            ResultSet getTagByNameResultSet = getTagByNameStatement.executeQuery();

            tagToBeReturned = this.getTagFromResultSet(getTagByNameResultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagToBeReturned;
    }

    @Override
    public boolean containsId(Long id) {
        boolean isIdPresent = false;
        String containsIdQuery =
                "SELECT id FROM tags WHERE id = ? ;";
        try (PreparedStatement containsIdStatement =
                     PreparedStatementProvider.prepareStatement(containsIdQuery)
        ) {
            containsIdStatement.setLong(1, id);
            ResultSet containsIdSet = containsIdStatement.executeQuery();
            if (containsIdSet.next()) {
                isIdPresent = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isIdPresent;
    }

    @Override
    public boolean nameContains(String name) {
        String returnedTagName = getByName(name).getTagName();
        return returnedTagName.equals(name);
    }
}
