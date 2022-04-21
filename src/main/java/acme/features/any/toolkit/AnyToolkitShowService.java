package acme.features.any.toolkit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.toolkit.Toolkit;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.roles.Any;
import acme.framework.services.AbstractShowService;

@Service
public class AnyToolkitShowService implements AbstractShowService<Any, Toolkit>{
	
	//Internal state
	
	@Autowired
	protected AnyToolkitRepository repository;
	
	//AbstractShowService<Any, Toolkit> interface
	
	@Override
	public boolean authorise(final Request<Toolkit> request) {
		assert request != null;

		boolean result;
		int id;
		Toolkit toolkit;

		id = request.getModel().getInteger("id");
		toolkit = this.repository.findToolkitById(id);
		result = toolkit.isPublished();
		return result;
	}

	@Override
	public void unbind(final Request<Toolkit> request, final Toolkit entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "code", "title", "description", "assemblyNotes", "totalPrice", "moreInfo");
	}

	@Override
	public Toolkit findOne(final Request<Toolkit> request) {
		assert request != null;

		Toolkit result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findToolkitById(id);

		return result;
	}
	
	
}