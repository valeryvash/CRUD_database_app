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
        long addedPostId = -1L;
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
        return this.get(addedPostId);
    }

    @Override
    public Post get(Long id) {
        Post postToBeReturned = new Post();
        List<Tag> postTags = new ArrayList<>();
        postToBeReturned.setPostTags(postTags);

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
                boolean isBatchUpdatesSupport = appToDatabaseConnection.getMetaData().supportsBatchUpdates();

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
        return this.get(entity.getId());
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
        String containsIdQuery =
                "SELECT id FROM posts WHERE id = ? ;";
        try (PreparedStatement containsIdStatement =
                     PreparedStatementProvider.prepareStatement(containsIdQuery)) {
            containsIdStatement.setLong(1, id);
            ResultSet containsIdSet = containsIdStatement.executeQuery();
            if (containsIdSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    @Override
    public List<Post> getPostsForWriter(Long id) {
        List<Post> postsToBeReturned = new ArrayList<>();

        String getWriterPostsQuery =
                "SELECT id as post_id, content as post_content, status as post_status, writer_id as fk_writer_id,t.id as tag_id,t.name as tag_name " +
                        "FROM posts p " +
                        "LEFT JOIN post_tags ptr on p.id = ptr.post_id " +
                        "LEFT JOIN tags t on ptr.tag_id = t.id " +
                        "WHERE writer_id = ? " +
                        "ORDER BY post_id ;";
        try (PreparedStatement getWriterPostsStatement =
                     PreparedStatementProvider.prepareStatement(getWriterPostsQuery)
        ) {
            getWriterPostsStatement.setLong(1, id);
            ResultSet answerFromDatabase = getWriterPostsStatement.executeQuery();

            Map<Long, Post> postMap = new HashMap<>();
            Writer writer = new Writer();
            writer.setId(id);

            while (answerFromDatabase.next()) {
                long postId = answerFromDatabase.getLong("post_id");
                if (!postMap.containsKey(postId)) {
                    Post currentLinePost = new Post();
                    currentLinePost.setId(postId);
                    currentLinePost.setPostContent(answerFromDatabase.getString("post_content"));
                    currentLinePost.setPostStatus(PostStatus.valueOf(answerFromDatabase.getString("post_status")));
                    currentLinePost.setWriter(writer);
                    currentLinePost.setPostTags(new ArrayList<>());
                    postMap.put(postId, currentLinePost);
                }

                long tagId = answerFromDatabase.getLong("tag_id");
                String tagName = answerFromDatabase.getString("tag_name");
                if (tagId != 0L & tagName != null) {
                    Tag t = new Tag();
                    t.setId(tagId);
                    t.setTagName(tagName);
                    postMap.get(postId).getPostTags().add(t);
                }
            }
            postsToBeReturned = new ArrayList<>(postMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postsToBeReturned;
    }

    @Override
    public Long getWriterId(Long postId) {
        long writerId = 0L;
        String getWriterIdQuery =
                "SELECT DISTINCT writer_id FROM posts p WHERE p.id = ? ;";
        try (PreparedStatement getWriterIdStatement =
                     PreparedStatementProvider.prepareStatement(getWriterIdQuery)
        ){
            getWriterIdStatement.setLong(1, postId);
            ResultSet rs = getWriterIdStatement.executeQuery();
            if (rs.next()) {
                writerId = rs.getLong("writer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writerId;
    }
}
