package cn.cuilan.bmp.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractServiceImpl<T> implements IBaseService<T> {

    // ================= insert ===================

    @Override
    public boolean insert(T t) {
        return false;
    }

    @Override
    public boolean insertAllColumn(T t) {
        return false;
    }

    @Override
    public boolean insertBatch(List<T> list) {
        return false;
    }

    @Override
    public boolean insertBatch(List<T> list, int i) {
        return false;
    }

    @Override
    public boolean insertOrUpdateBatch(List<T> list) {
        return false;
    }

    @Override
    public boolean insertOrUpdateBatch(List<T> list, int i) {
        return false;
    }

    @Override
    public boolean insertOrUpdateAllColumnBatch(List<T> list) {
        return false;
    }

    @Override
    public boolean insertOrUpdateAllColumnBatch(List<T> list, int i) {
        return false;
    }

    // ================= delete ===================

    @Override
    public boolean deleteById(Serializable serializable) {
        return false;
    }

    @Override
    public boolean deleteByMap(Map<String, Object> map) {
        return false;
    }

    @Override
    public boolean delete(Wrapper<T> wrapper) {
        return false;
    }

    @Override
    public boolean deleteBatchIds(Collection<? extends Serializable> collection) {
        return false;
    }

    // ================= update ===================

    @Override
    public boolean updateById(T t) {
        return false;
    }

    @Override
    public boolean updateAllColumnById(T t) {
        return false;
    }

    @Override
    public boolean update(T t, Wrapper<T> wrapper) {
        return false;
    }

    @Override
    public boolean updateForSet(String s, Wrapper<T> wrapper) {
        return false;
    }

    @Override
    public boolean updateBatchById(List<T> list) {
        return false;
    }

    @Override
    public boolean updateBatchById(List<T> list, int i) {
        return false;
    }

    @Override
    public boolean updateAllColumnBatchById(List<T> list) {
        return false;
    }

    @Override
    public boolean updateAllColumnBatchById(List<T> list, int i) {
        return false;
    }

    @Override
    public boolean insertOrUpdate(T t) {
        return false;
    }

    @Override
    public boolean insertOrUpdateAllColumn(T t) {
        return false;
    }

    // ================= select ===================

    @Override
    public T selectById(Serializable serializable) {
        return null;
    }

    @Override
    public List<T> selectBatchIds(Collection<? extends Serializable> collection) {
        return null;
    }

    @Override
    public List<T> selectByMap(Map<String, Object> map) {
        return null;
    }

    @Override
    public T selectOne(Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public Map<String, Object> selectMap(Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public Object selectObj(Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public int selectCount(Wrapper<T> wrapper) {
        return 0;
    }

    @Override
    public List<T> selectList(Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public Page<T> selectPage(Page<T> page) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public List<Object> selectObjs(Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public Page<Map<String, Object>> selectMapsPage(Page page, Wrapper<T> wrapper) {
        return null;
    }

    @Override
    public Page<T> selectPage(Page<T> page, Wrapper<T> wrapper) {
        return null;
    }
}
