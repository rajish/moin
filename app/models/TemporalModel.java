/**
 * 
 */
package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@MappedSuperclass
public class TemporalModel extends Model {

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdAt", nullable = false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedAt", nullable = false)
	private Date updatedAt;

	@PrePersist
	protected void onCreate() {
		updatedAt = createdAt = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
}
