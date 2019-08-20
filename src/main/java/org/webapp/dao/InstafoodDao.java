package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Instafood;

import javax.sql.DataSource;

import java.util.*;

@Repository
public class InstafoodDao implements Dao {
    @Autowired
    DataSource dataSource;
    @Autowired
    DataSourceContext dataSourceContext;

    @Override
    public void save(Object model) {    //model = 셋팅된 Instafood객체
        try {
            Instafood instafood = (Instafood) model;
            dataSource = dataSourceContext.dataSource();
            String sql = "insert into instafood(station, post, photoURL, instafood.date) values (?, ?, ?, ?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, instafood.getStation(), instafood.getPost(), instafood.getPhotoURL(), instafood.getDate());
        } catch (Exception e) {
            System.out.println("instafood save fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> findByParam(Object parameter) {   //parameter = List<Object> parameter [0] = station명, [1] = date날짜
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from instafood where station = ? AND instafood.date = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Object> parameterList = (List<Object>) parameter;

            List<Object> returnList = new ArrayList<>();
            returnList.add(jdbcTemplate.queryForObject(sql, new Object[] {parameterList.get(0), parameterList.get(1)},
                    new InstafoodMapper()));
            return returnList;
        } catch (Exception e) {
            System.out.println("instafood find fail!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {  //parameter =
//        try {
//            dataSource = dataSourceContext.dataSource();
//            String sql = "delete from instafood where instafood.key = ?";
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            jdbcTemplate.update(sql, parameter);
//        } catch (Exception e) {
//            System.out.println("instafood delete fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public void update(Object model) {  //parameter = 사이즈2인 Map ===> Map[0] = <바꿀column, udpate할 값>, Map[1] = <key, null>
//        try {
//            Map<Object, Object> instafood = (Map<Object, Object>) model;
//            Set<Object> keySet = instafood.keySet();
//            Iterator<Object> key = keySet.iterator();
//            String sql = "";
//
//            dataSource = dataSourceContext.dataSource();
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            if (instafood.containsKey("post")) {
//                sql = "update instafood set post = ? where instafood.key = ?";
//            }
//            else if (instafood.containsKey("photoURL")) {
//                sql = "update instafood set photoURL = ? where instafood.key = ?";
//            }
//            else if (instafood.containsKey("date")) {
//                sql = "update instafood set instafood.date = ? where instafood.key = ?";
//            }
//            if (key.hasNext()) {
//                jdbcTemplate.update(sql, instafood.get(key.next()), key.next());
//            }
//        } catch (Exception e) {
//            System.out.println("instafood update fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from instafood";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Instafood> totlaRows = jdbcTemplate.query(sql, new InstafoodMapper());
            return totlaRows;
        } catch (Exception e) {
            System.out.println("instafood findAll fail!");
            e.printStackTrace();
            return null;
        }
    }
}
