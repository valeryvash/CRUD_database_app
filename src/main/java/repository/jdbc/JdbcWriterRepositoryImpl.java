package repository.jdbc;

import model.Post;
import model.Tag;
import model.Writer;
import repository.WriterRepository;
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
    public Writer add(Writer entity) {

        String writerTableQuery =
                "INSERT INTO writers (name) " +
                        "VALUE (?) ;";
        String postTableQuery =
                "INSERT INTO posts (content,status,writer_id) " +
                        "VALUE (?,?,?) ;";
        String postTagRelationTableQuery =
                "INSERT INTO post_tags (post_id, tag_id) " +
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

            ResultSet writerGeneratedKey = addWriterTableStatement.getGeneratedKeys();
            long addedWriterId = -1L;
            if (writerGeneratedKey.next()) {
                addedWriterId = writerGeneratedKey.getLong("GENERATED_KEY");
            }
            Writer w = get(addedWriterId);

            if (doesWriterHavePosts) {

                for (Post writerPost : writerPosts) {
                    writerPost.setWriter(w);

                    addWriterPostTableStatement.setString(1, writerPost.getPostContent());
                    addWriterPostTableStatement.setString(2, writerPost.getPostStatus().name());
                    addWriterPostTableStatement.setLong(3, addedWriterId);
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
            return w;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Writer();
    }

    @Override
    public Writer get(Long id) {

        Writer writerToBeReturned = new Writer();
        String getWriterQuery =
                "SELECT name " +
                        "FROM writers " +
                        "WHERE id = ? ;";
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
                    if (Objects.equals(p.getWriter().getId(), id)){
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
    public Writer update(Writer entity) {

        String updateWriterNameQuery =
                "UPDATE writers SET " +
                        "name = ? " +
                        "WHERE id = ? ;";
        String deleteWriterPostsQuery =
                "DELETE FROM posts " +
                        "WHERE writer_id = ? ;";
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

        return get(entity.getId());
    }

    @Override
    public void remove(Long id) {

        String deleteWriterQuery =
                "DELETE FROM writers " +
                        "WHERE id = ? ;";
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
                "SELECT id, name " +
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
                        if (Objects.equals(p.getWriter().getId(), writerToBeAddedToList.getId())) {
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

    @Override
    public boolean nameContains(String name) {
        return false;
    }

    @Override
    public boolean containsId(Long id) {
        boolean isIdContains = false;
        String containsIdQuery =
                "SELECT id FROM writers WHERE id = ? ;";
        try (PreparedStatement preparedStatement =
                     PreparedStatementProvider.prepareStatement(containsIdQuery)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isIdContains = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isIdContains;
    }

    @Override
    public Writer getByName(String name) {
        long writerId = -1L;

        String getIdByNameQuery =
                "SELECT id " +
                        "FROM writers " +
                        "WHERE name = ? ;";
        try (PreparedStatement preparedStatement =
                     PreparedStatementProvider.prepareStatement(getIdByNameQuery)
        ) {
            preparedStatement.setString(1, name);
            ResultSet getByNameResultSet = preparedStatement.executeQuery();
            if (getByNameResultSet.next()) {
                writerId = getByNameResultSet.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return get(writerId);
    }
}
