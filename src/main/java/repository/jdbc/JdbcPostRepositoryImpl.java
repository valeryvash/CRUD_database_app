package repository.jdbc;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import repository.PostRepository;
import util.PreparedStatementProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcPostRepositoryImpl implements PostRepository {

    @Override
    public Post add(Post entity) {

        String postTableQuery =
                "INSERT INTO posts (content,status,writer_id) " +
                        "VALUE (?,?,?) ;";
        String postTagRelationTableQuery =
                "INSERT INTO post_tags (post_id, tag_id) " +
                        "VALUE (?,?) ;";

        try (
                PreparedStatement addPostStatement =
                        PreparedStatementProvider.prepareStatement(
                                postTableQuery,
                                PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement addPostTagRelationStatement =
                        PreparedStatementProvider.prepareStatement(
                                postTagRelationTableQuery);
        ) {
            Connection appDatabaseConnection = addPostStatement.getConnection();
            appDatabaseConnection.setAutoCommit(false);

            addPostStatement.setString(1, entity.getPostContent());
            addPostStatement.setString(2, entity.getPostStatus().name());
            addPostStatement.setLong(3, entity.getWriter().getId());
            addPostStatement.executeUpdate();

            appDatabaseConnection.commit();

            ResultSet postGeneratedKey = addPostStatement.getGeneratedKeys();
            long addedPostId = -1L;
            if (postGeneratedKey.next()) {
                addedPostId = postGeneratedKey.getLong("GENERATED_KEY");
            }

            List<Tag> postTags = entity.getPostTags();
            if (!postTags.isEmpty()) {
                boolean isBatchUpdateSupported = appDatabaseConnection.getMetaData().supportsBatchUpdates();
                if (isBatchUpdateSupported) {
                    for (Tag t : postTags) {
                        long postTagId = t.getId();
                        addPostTagRelationStatement.setLong(1, addedPostId);
                        addPostTagRelationStatement.setLong(2, postTagId);
                        addPostTagRelationStatement.addBatch();
                    }
                    addPostTagRelationStatement.executeBatch();
                } else {
                    for (Tag t : postTags) {
                        long postTagId = t.getId();
                        addPostTagRelationStatement.setLong(1, addedPostId);
                        addPostTagRelationStatement.setLong(2, postTagId);
                        addPostTagRelationStatement.executeUpdate();
                    }
                }
                appDatabaseConnection.commit();
            }
            appDatabaseConnection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Post get(Long id) {

        Post postToBeReturned = new Post();
        List<Tag> postTags = postToBeReturned.getPostTags();
        String getPostQuery =
                "SELECT p.id as post_id,content as post_content,status as post_status,writer_id as fk_writer_id,t.id as tag_id,t.name as tag_name " +
                        "FROM posts p " +
                        "LEFT JOIN post_tags ptr on p.id = ptr.post_id " +
                        "LEFT JOIN tags t on ptr.tag_id = t.id " +
                        "WHERE post_id = ? ;";
        try (PreparedStatement getPostStatement =
                     PreparedStatementProvider.prepareStatement(getPostQuery)) {
            getPostStatement.setLong(1, id);

            ResultSet getPostResultSet = getPostStatement.executeQuery();

            if (getPostResultSet.next()) {
                postToBeReturned.setId(getPostResultSet.getLong("post_id"));
                postToBeReturned.setPostContent(getPostResultSet.getString("post_content"));
                postToBeReturned.setPostStatus(PostStatus.valueOf(getPostResultSet.getString("post_status")));
                Writer w = new Writer();
                w.setId(getPostResultSet.getLong("fk_writer_id"));
                postToBeReturned.setWriter(w);

                do {
                    long tagId = getPostResultSet.getLong("tag_id");
                    String tagName = getPostResultSet.getString("tag_name");

                    if (tagId != 0L & tagName != null) {
                        Tag tagToBeAdded = new Tag();
                        tagToBeAdded.setId(tagId);
                        tagToBeAdded.setTagName(tagName);
                        postTags.add(tagToBeAdded);
                    }
                } while (getPostResultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return postToBeReturned;
    }

    @Override
    public Post update(Post entity) {
        List<Tag> postTags = entity.getPostTags();
        boolean isTagListNotEmpty = !postTags.isEmpty();

        String updatePostQuery =
                "UPDATE posts SET " +
                        "content = ? ," +
                        "status = ? ," +
                        "writer_id = ? " +
                        "WHERE id = ? ;";
        String deletePostTagRelationQuery =
                "DELETE FROM post_tags " +
                        "WHERE post_id = ? ;";
        String insertPostTagRelationQuery =
                "INSERT INTO post_tags (post_id, tag_id) " +
                        "VALUE (?,?);";
        try (PreparedStatement updatePostStatement =
                     PreparedStatementProvider.prepareStatement(updatePostQuery);
             PreparedStatement deletePostTagRelationStatement =
                     PreparedStatementProvider.prepareStatement(deletePostTagRelationQuery);
             PreparedStatement insertPostTagRelationStatement =
                     PreparedStatementProvider.prepareStatement(insertPostTagRelationQuery);
        ) {
            Connection appToDatabaseConnection = updatePostStatement.getConnection();
            appToDatabaseConnection.setAutoCommit(false);

            updatePostStatement.setString(1, entity.getPostContent());
            updatePostStatement.setString(2, entity.getPostStatus().name());
            updatePostStatement.setLong(3, entity.getWriter().getId());
            updatePostStatement.setLong(4, entity.getId());
            updatePostStatement.executeUpdate();

            deletePostTagRelationStatement.setLong(1, entity.getId());
            deletePostTagRelationStatement.executeUpdate();

            if (isTagListNotEmpty) {
                final boolean isBatchUpdatesSupport = appToDatabaseConnection.getMetaData().supportsBatchUpdates();

                long postId = entity.getId();
                for (Tag t : postTags) {
                    long tagId = t.getId();
                    insertPostTagRelationStatement.setLong(1, postId);
                    insertPostTagRelationStatement.setLong(2, tagId);

                    if (isBatchUpdatesSupport) {
                        insertPostTagRelationStatement.addBatch();
                    } else {
                        insertPostTagRelationStatement.executeUpdate();
                    }
                }
                if (isBatchUpdatesSupport) {
                    insertPostTagRelationStatement.executeBatch();
                }
            }
            appToDatabaseConnection.commit();
            appToDatabaseConnection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void remove(Long id) {

        String deletePostQuery =
                "DELETE FROM posts " +
                        "WHERE id = ? ;";
        try (PreparedStatement deletePostStatement =
                     PreparedStatementProvider.prepareStatement(deletePostQuery)) {
            deletePostStatement.setLong(1, id);
            deletePostStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> listToBeReturned = new ArrayList<>();

        String getAllPostsQuery =
                "SELECT id as post_id, content as post_content, status as post_status, writer_id as fk_writer_id,t.id as tag_id,t.name as tag_name " +
                        "FROM posts p " +
                        "LEFT JOIN post_tags ptr on p.id = ptr.post_id " +
                        "LEFT JOIN tags t on ptr.tag_id = t.id " +
                        "ORDER BY post_id ;";
        try (PreparedStatement getAllPostStatement =
                     PreparedStatementProvider.prepareStatement(getAllPostsQuery);
             ResultSet answerFromDatabase = getAllPostStatement.executeQuery()) {

            Post currentPost = new Post();
            List<Tag> currentPostTags = currentPost.getPostTags();
            Map<Long, Tag> tagCache = new HashMap<>();
            while (answerFromDatabase.next()) {
                long currentLinePostId = answerFromDatabase.getLong("post_id");
                if (currentLinePostId != currentPost.getId()) {
                    currentPost = new Post();
                    currentPostTags = currentPost.getPostTags();
                    listToBeReturned.add(currentPost);

                    currentPost.setId(currentLinePostId);
                    currentPost.setPostContent(answerFromDatabase.getString("post_content"));
                    currentPost.setPostStatus(PostStatus.valueOf(answerFromDatabase.getString("post_status")));
                    Writer w = new Writer();
                    w.setId(answerFromDatabase.getLong("fk_writer_id"));
                    currentPost.setWriter(w);
                }

                long tagId = answerFromDatabase.getLong("tag_id");
                String tagName = answerFromDatabase.getString("tag_name");
                if (tagId != 0L & tagName != null) {
                    if (tagCache.containsKey(tagId)) {
                        currentPostTags.add(tagCache.get(tagId));
                    } else {
                        Tag tagToBeAdded = new Tag();
                        tagToBeAdded.setId(tagId);
                        tagToBeAdded.setTagName(tagName);
                        currentPostTags.add(tagToBeAdded);
                        tagCache.put(tagId, tagToBeAdded);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listToBeReturned;
    }

    @Override
    public boolean containsId(Long id) {
        boolean isIdPresented = false;
        String containsIdQuery =
                "SELECT id FROM posts WHERE id = ? ;";
        try (PreparedStatement containsIdStatement =
                     PreparedStatementProvider.prepareStatement(containsIdQuery)) {
            containsIdStatement.setLong(1, id);
            ResultSet containsIdSet = containsIdStatement.executeQuery();
            if (containsIdSet.next()) {
                isIdPresented = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isIdPresented;
    }

    @Override
    public void deleteByStatus(PostStatus postStatus) {
        String statusString = postStatus.name();
        String deletePostByStatusQuery =
                "DELETE FROM posts " +
                        "WHERE status = ? ;";
        try (PreparedStatement deletePostByStatusStatement =
                     PreparedStatementProvider.prepareStatement(deletePostByStatusQuery)) {
            deletePostByStatusStatement.setString(1, statusString);
            deletePostByStatusStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
