package co.markhed.demo.messaging.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import static javax.persistence.GenerationType.IDENTITY;

@MappedSuperclass
public class BaseEntity {

    @ApiModelProperty(notes = "The database-generated message ID")
    @Id
    @GeneratedValue(strategy = IDENTITY)
    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
