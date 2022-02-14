package repository;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import util.PreparedStatementProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcWriterRepositoryImpl implements WriterRepository {

    JdbcPostRepositoryImpl jdbcPostRepository = new JdbcPostRepositoryImpl();

    @Override
    public void add(Writer entity) {
        if (entity.getId() != -1L) {
            throw new IllegalArgumentException("Id value is " + entity.getId() + ", but shall be -1L");
        }

        String writerTableQuery =
                "INSERT INTO writers (writer_name) " +
                        "VALUE (?) ;";
        String postTableQuery =
                "INSERT INTO posts (post_content,post_status,fk_writer_id) " +
                        "VALUE (?,?,?) ;";
        String postTagRelationTableQuery =
                "INSERT INTO post_tag_relation (fk_post_id, fk_tag_id) " +
                        "VALUE (?,?) ;";
        List<Post> writerPosts = entity.getWriterPosts();
        boolean doesWriterHavePosts = !writerPosts.isEmpty();
        try (PreparedStatement addWriterTableStatement =
                     PreparedStatementProvider.prepareStatement(writerTableQuery,
                             PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement addWriterPostTableStatement =
                     PreparedStatementProvider.prepareStatement(postTableQuery,
                             PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement addPostTagRelationStatement =
                     PreparedStatementProvider.prepareStatement(postTagRelationTableQuery)
        ) {

            Connection appDatabaseConnection = addWriterTableStatement.getConnection();
            appDatabaseConnection.setAutoCommit(false);

            addWriterTableStatement.setString(1, entity.getWriterName());
            addWriterTableStatement.executeUpdate();

            appDatabaseConnection.commit();

            if (doesWriterHavePosts) {
                ResultSet writerGeneratedKey = addWriterTableStatement.getGeneratedKeys();
                long addedWriterId = -1L;
                if (writerGeneratedKey.next()) {
                    addedWriterId = writerGeneratedKey.getLong("GENERATED_KEY");
                }

                for (Post writerPost : writerPosts) {
                    writerPost.setFkWriterId(addedWriterId);

                    addWriterPostTableStatement.setString(1, writerPost.getPostContent());
                    addWriterPostTableStatement.setString(2, writerPost.getPostStatus().name());
                    addWriterPostTableStatement.setLong(3, writerPost.getFkWriterId());
                    addWriterPostTableStatement.executeUpdate();

                    appDatabaseConnection.commit();

                    ResultSet postGeneratedKey = addWriterPostTableStatement.getGeneratedKeys();
                    long addedPostId = -1L;
                    if (postGeneratedKey.next()) {
                        addedPostId = postGeneratedKey.getLong("GENERATED_KEY");
                    }

                    List<Tag> postTags = writerPost.getPostTags();
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
                }
            }
            appDatabaseConnection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Writer get(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall have a positive value");
        }

        Writer writerToBeReturned = new Writer();
        String getWriterQuery =
                "SELECT writer_name " +
                        "FROM writers " +
                        "WHERE writer_id = ? ;";
        try (PreparedStatement getWriterStatement =
                     PreparedStatementProvider.prepareStatement(getWriterQuery)) {
            List<Post> allPosts = jdbcPostRepository.getAll();
            boolean dbHasPosts = !allPosts.isEmpty();

            getWriterStatement.setLong(1, id);

            ResultSet getWriterResultSet = getWriterStatement.executeQuery();

            if (getWriterResultSet.next()) {
                writerToBeReturned.setId(id);
                writerToBeReturned.setWriterName(getWriterResultSet.getString("writer_name"));
            }

            if (dbHasPosts) {
                List<Post> writerPosts = writerToBeReturned.getWriterPosts();
                for (Post p : allPosts) {
                    if (Objects.equals(p.getFkWriterId(), id)){
                        writerPosts.add(p);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return writerToBeReturned;
    }

    @Override
    public void update(Writer entity) {
        if (entity.getId() < 1L) {
            throw new IllegalArgumentException("Id shall be a positive value");
        }

        String updateWriterNameQuery =
                "UPDATE writers SET " +
                        "writer_name = ? " +
                        "WHERE writer_id = ? ;";
        String deleteWriterPostsQuery =
                "DELETE FROM posts " +
                        "WHERE fk_writer_id = ? ;";
        try (PreparedStatement updateWriterNameStatement =
                     PreparedStatementProvider.prepareStatement(updateWriterNameQuery);
             PreparedStatement deleteWriterPostsStatement =
                     PreparedStatementProvider.prepareStatement(deleteWriterPostsQuery)
        ) {
            updateWriterNameStatement.setString(1,entity.getWriterName());
            updateWriterNameStatement.setLong(2,entity.getId());
            updateWriterNameStatement.executeUpdate();

            deleteWriterPostsStatement.setLong(1, entity.getId());
            deleteWriterPostsStatement.executeUpdate();

            boolean doesWriterHasPosts = !entity.getWriterPosts().isEmpty();
            if (doesWriterHasPosts) {
                List<Post> writerPosts = entity.getWriterPosts();
                for (Post p : writerPosts) {
                    jdbcPostRepository.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Long id) {
        if (id < 1L) {
            throw new IllegalArgumentException("Id shall have a positive value");
        }

        String deleteWriterQuery =
                "DELETE FROM writers " +
                        "WHERE writer_id = ? ;";
        try (PreparedStatement deleteWriterStatement =
                     PreparedStatementProvider.prepareStatement(deleteWriterQuery)) {
            deleteWriterStatement.setLong(1, id);
            deleteWriterStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Writer> getAll() {
        List<Writer> listToBeReturned = new ArrayList<>();

        List<Post> allPostList = jdbcPostRepository.getAll();

        String getAllWritersQuery =
                "SELECT writer_id, writer_name " +
                        "FROM writers;";
        try (PreparedStatement getAllWritersStatement =
                     PreparedStatementProvider.prepareStatement(getAllWritersQuery);
             ResultSet allWritersResultSet = getAllWritersStatement.executeQuery();
        ) {
            List<Post> allPosts = jdbcPostRepository.getAll();
            boolean dbHasPosts = !allPosts.isEmpty();
            while (allWritersResultSet.next()) {
                Writer writerToBeAddedToList = new Writer();
                listToBeReturned.add(writerToBeAddedToList);

                long writerId = allWritersResultSet.getLong("writer_id");
                writerToBeAddedToList.setId(writerId);
                writerToBeAddedToList.setWriterName(allWritersResultSet.getString("writer_name"));

                if (dbHasPosts) {
                    List<Post> writerPosts = writerToBeAddedToList.getWriterPosts();
                    for (Post p : allPosts) {
                        if (Objects.equals(p.getFkWriterId(), writerToBeAddedToList.getId())) {
                            writerPosts.add(p);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listToBeReturned;
    }
}
