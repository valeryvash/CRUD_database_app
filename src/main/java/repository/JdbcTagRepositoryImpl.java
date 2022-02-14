package repository;

import model.Tag;
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
            t.setId(resultSet.getLong("tag_id"));
            t.setTagName(resultSet.getString("tag_name"));
            return t;
        }
        return t;
    }

    private List<Tag> getListOfTagsFromResultSet(ResultSet resultSet) throws SQLException {
        List<Tag> tags = new ArrayList<>();
        while (resultSet.next()) {
            Tag t = new Tag();
            t.setId(resultSet.getLong("tag_id"));
            t.setTagName(resultSet.getString("tag_name"));
            tags.add(t);
        }
        return tags;
    }

    @Override
    public void add(Tag entity) {
        if (entity.getId() != -1L) {
            throw new IllegalArgumentException("Id value is " + entity.getId() + ", but shall be -1L");
        }

        String addTagQuery = "INSERT INTO tags (tag_name) VALUE (?);";
        try (PreparedStatement addTagStatement =
                     PreparedStatementProvider.prepareStatement(addTagQuery)) {
            addTagStatement.setString(1, entity.getTagName());
            addTagStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Tag get(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("id shall be positive value");
        }

        Tag tagToBeReturned = new Tag();
        String getTagQuery = "SELECT tag_id, tag_name FROM tags WHERE tag_id = ? ;";
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
    public void update(Tag entity) {
        if (entity.getId() < 1L) {
            throw new IllegalArgumentException("Id shall be a positive value");
        }

        String updateTagQuery =
                "UPDATE tags SET tags.tag_name = ? " +
                        "WHERE tags.tag_id = ? ;";
        try (PreparedStatement updateTagStatement =
                     PreparedStatementProvider.prepareStatement(updateTagQuery)) {
            updateTagStatement.setString(1, entity.getTagName());
            updateTagStatement.setLong(2, entity.getId());
            updateTagStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall be positive value");
        }

        String deleteTagQuery = "DELETE FROM tags WHERE tags.tag_id = ? ;";
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

        String getAllTagsQuery = "SELECT tag_id, tag_name FROM tags;";
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
}
