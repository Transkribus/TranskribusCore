package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.transkribus.core.util.CoreUtils;

@Entity
@Table(name = "JOB_IMPL_REGISTRY")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpJobImplRegistry {
	@Id
	@Column(name="job_impl_registry_id")
	private int jobImplRegistryId;
	
	@Column(name="job_impl")
	private String jobImpl;
	
	@Column(name="job_tasks")
	private String jobTasks;
	
	@Column(name="job_type")
	private String jobType;
	
	@Transient
	private TrpCreditCosts costs;
	
	@Column
	@XmlTransient
	@Transient
	private List<Integer> users;
	
	public TrpJobImplRegistry() {
		this.users = new ArrayList<>(0);
	}

	public TrpJobImplRegistry(int jobImplRegistryId, String jobImpl, String jobTasks, String jobType) {
		this();
		this.jobImplRegistryId = jobImplRegistryId;
		this.jobImpl = jobImpl;
		this.jobTasks = jobTasks;
		this.jobType = jobType;
	}

	public int getJobImplRegistryId() {
		return jobImplRegistryId;
	}

	public void setJobImplRegistryId(int jobImplRegistryId) {
		this.jobImplRegistryId = jobImplRegistryId;
	}

	public String getJobImpl() {
		return jobImpl;
	}

	public void setJobImpl(String jobImpl) {
		this.jobImpl = jobImpl;
	}

	public String getJobTasks() {
		return jobTasks;
	}

	public void setJobTasks(String jobTasks) {
		this.jobTasks = jobTasks;
	}
	
	public List<String> getJobTasksList() {
		return CoreUtils.parseStringList(jobTasks, true);
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public TrpCreditCosts getCosts() {
		return costs;
	}

	public void setCosts(TrpCreditCosts costs) {
		this.costs = costs;
	}

	/**
	 * Use the method from the DAO to check the relation table instead of selecting all objects and checking each one for allowance.
	 * 
	 * @param userId
	 * @return
	 */
	@Deprecated
	public boolean isUserAllowed(Integer userId) {
		//List<String> ul = getUsersList();
		List<Integer> ul = getUsers();
		if (ul.isEmpty()) // empty userlist means no restriction -> add some dummy user like "deaadmin" with userId = 1 to restrict module to admins only!
			return true;
		
		return ul.contains(userId);
	}

	@Transient
	public List<Integer> getUsers() {
		return users;
	}

	@Transient
	public void setUsers(List<Integer> list) {
		this.users = list;
	}

	@Override
	public String toString() {
		return "TrpJobImplRegistry [jobImplRegistryId=" + jobImplRegistryId + ", jobImpl=" + jobImpl + ", jobTasks="
				+ jobTasks + ", jobType=" + jobType + ", costs=" + costs + ", users=" + users + "]";
	}
}
