package acme.features.inventor.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.item.Item;
import acme.entities.item.ItemType;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractUpdateService;
import acme.roles.Inventor;

@Service
public class InventorToolUpdateService implements AbstractUpdateService<Inventor, Item>{
	
	// Internal state ------------------------------------------------
	
			@Autowired
			protected InventorItemRepository itemRepo;
			
			
	@Override
	public boolean authorise(final Request<Item> request) {
		
		assert request != null;
		final boolean result;
		int itemId;
		final Item item;
		final Inventor inventor;
		
		itemId=request.getModel().getInteger("id");
		item=this.itemRepo.findItemById(itemId);
		inventor=item.getInventor();
		
		result= !item.isPublished() && request.isPrincipal(inventor);
		
		
		return result;
	}
	
	@Override
	public void bind(final Request<Item> request, final Item entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		entity.setItemType(ItemType.TOOL);
		
		request.bind(entity, errors, "name","code",  "technology", "description", "retailPrice", "link");
	}
	
	@Override
	public void unbind(final Request<Item> request, final Item entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		entity.setItemType(ItemType.TOOL);
		
		request.unbind(entity, model, "name","code", "technology", "description", "retailPrice", "link");
		
	}
	
	@Override
	public Item findOne(final Request<Item> request) {
		assert request != null;

		Item result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.itemRepo.findItemById(id);

		return result;
	}
	
	@Override
	public void validate(final Request<Item> request, final Item entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		if(!errors.hasErrors("code")) {
			
			final Item existing = this.itemRepo.findItemByCode(entity.getCode());
			errors.state(request, existing == null || existing.getId() == entity.getId(), "code", "inventor.item.form.error.duplicated");
			final Item item = this.itemRepo.findItemById(entity.getId());
			errors.state(request, item.getCode().equals(entity.getCode()), "code", "inventor.item.form.error.codeChanged");
			
			
		}
		if(!errors.hasErrors("retailPrice")) {
			final boolean accepted = this.itemRepo.findAcceptedCurrencies().matches("(.*)" + entity.getRetailPrice().getCurrency()+ "(.*)");
			errors.state(request, accepted, "retailPrice", "inventor.item.form.error.currency");
			errors.state(request, entity.getRetailPrice().getAmount()>0, "retailPrice", "inventor.item.form.error.negative");
		}
		
	}
	
	@Override
	public void update(final Request<Item> request, final Item entity) {
		assert request != null;
		assert entity != null;

		this.itemRepo.save(entity);
		
	}
	

	

}
