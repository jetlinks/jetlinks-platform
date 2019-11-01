package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.api.crud.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;


@Getter
@Setter
@Table(name = "dev_mesh_detail", indexes = {
        @Index(name = "idx_dm_mesh_id", columnList = "mesh_id"),
        @Index(name = "idx_dm_path", columnList = "path"),
        @Index(name = "idx_dm_parent_id", columnList = "parent_id")
})
public class DeviceMeshDetailEntity extends GenericEntity<String> {

    @Column(name = "mesh_id")
    private String meshId;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "path", length = 128)
    private String path;

}
