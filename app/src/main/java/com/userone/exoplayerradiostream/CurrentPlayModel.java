package com.userone.exoplayerradiostream;

/**
 * Created by userone on 5/16/2018.
 */

public class CurrentPlayModel {
    private String name;
    private String description;
    private Integer instance_id;
    private Integer id;
    private String image_path;

    public CurrentPlayModel(String name, String description, Integer instance_id, Integer id, String image_path) {
        this.name = name;
        this.description = description;
        this.instance_id = instance_id;
        this.id = id;
        this.image_path = image_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(Integer instance_id) {
        this.instance_id = instance_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
